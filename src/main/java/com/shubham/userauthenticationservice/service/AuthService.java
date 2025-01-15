package com.shubham.userauthenticationservice.service;

import com.shubham.userauthenticationservice.exceptions.PasswordMismatchException;
import com.shubham.userauthenticationservice.exceptions.UserAlreadyExistsException;
import com.shubham.userauthenticationservice.exceptions.UserNotFoundException;
import com.shubham.userauthenticationservice.models.Role;
import com.shubham.userauthenticationservice.models.User;
import com.shubham.userauthenticationservice.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService implements IAuthService {

    @Autowired
    private UserRepo userRepo;

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
    public User login(String email, String password) throws UserNotFoundException, PasswordMismatchException {

        Optional<User> optionalUser = userRepo.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User not found, please try with valid email");
        }

        String storePassword = optionalUser.get().getPassword();
//        if(!password.equals(storePassword)){
        if(!bCryptPasswordEncoder.matches(password, storePassword)){
            throw new PasswordMismatchException("Please try with correct password");
        }

        return optionalUser.get();
    }
}
