package com.shubham.userauthenticationservice.service;

import com.shubham.userauthenticationservice.exceptions.PasswordMismatchException;
import com.shubham.userauthenticationservice.exceptions.UserAlreadyExistsException;
import com.shubham.userauthenticationservice.exceptions.UserNotFoundException;
import com.shubham.userauthenticationservice.models.Role;
import com.shubham.userauthenticationservice.models.Session;
import com.shubham.userauthenticationservice.models.Status;
import com.shubham.userauthenticationservice.models.User;
import com.shubham.userauthenticationservice.repos.SessionRepo;
import com.shubham.userauthenticationservice.repos.UserRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class AuthService implements IAuthService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SessionRepo sessionRepo;

    @Autowired
    private SecretKey secretKey;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder; //use to encrypt the password

    @Override
    public User signUp(String email, String password) throws UserAlreadyExistsException {

        Optional<User> optionalUser = userRepo.findByEmail(email);
        if (optionalUser.isPresent()) {
            throw new UserAlreadyExistsException("User already exists, please try with different email");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setCreatedAt(new Date());
        user.setLastUpdatedAt(new Date());
        Role role = new Role();
        role.setValue("CUSTOMER");
        user.setRoles(List.of(role));

        //persisting into db
        userRepo.save(user);

        return user;
    }

    @Override
    public Pair<User,String> login(String email, String password) throws UserNotFoundException, PasswordMismatchException {

        Optional<User> optionalUser = userRepo.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User not found, please try with valid email");
        }

        String storePassword = optionalUser.get().getPassword();
//        if(!password.equals(storePassword)){
        if(!bCryptPasswordEncoder.matches(password, storePassword)){
            throw new PasswordMismatchException("Please try with correct password");
        }

        //Generating JWT
//        String message = "{\n" +
//                "   \"email\": \"shubham2@gmail.com\",\n" +
//                "   \"roles\": [\n" +
//                "      \"learner\",\n" +
//                "      \"buddy\"\n" +
//                "   ],\n" +
//                "   \"expirationDate\": \"2ndApril2025\"\n" +
//                "}";
//
//        byte[] content = message.getBytes(StandardCharsets.UTF_8);
        //String token = Jwts.builder().content(content).compact();

        Map<String, Object> payload = new HashMap<>();
        Long nowInMills = System.currentTimeMillis();
        payload.put("iat", nowInMills);
        payload.put("exp", nowInMills + 100000);
        payload.put("userId", optionalUser.get().getId());
        payload.put("iss", "scaler");
        payload.put("scope", optionalUser.get().getRoles());

//        MacAlgorithm algorithm = Jwts.SIG.HS256;
//        SecretKey secretKey = algorithm.key().build();
        String token = Jwts.builder().claims(payload).signWith(secretKey).compact();

        Session session = new Session();
        session.setToken(token);
        session.setUser(optionalUser.get());
        session.setCreatedAt(new Date());
        session.setLastUpdatedAt(new Date());
        session.setStatus(Status.ACTIVE);
        sessionRepo.save(session); //persisting into db

        return new Pair<User, String>(optionalUser.get(), token);
    }

    public Boolean validateToken(String token, Long userId){

        Optional<Session> optionalSession = sessionRepo.findByTokenAndUser_Id(token, userId);

        if(optionalSession.isEmpty()){
            return false;
        }

        JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();
        Claims claims = jwtParser.parseSignedClaims(token).getPayload();

        Long tokenExpiry = (Long) claims.get("exp"); //getting expiry from token
        Long currentTime = System.currentTimeMillis();

        if(currentTime > tokenExpiry){
            Session session = optionalSession.get();
            session.setStatus(Status.INACTIVE);
            sessionRepo.save(session);
            return false;
        }

        return true;
    }
}
