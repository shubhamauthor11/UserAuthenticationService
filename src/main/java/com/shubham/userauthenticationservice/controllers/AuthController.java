package com.shubham.userauthenticationservice.controllers;

import com.shubham.userauthenticationservice.dtos.LoginRequest;
import com.shubham.userauthenticationservice.dtos.SignupRequest;
import com.shubham.userauthenticationservice.dtos.UserDto;
import com.shubham.userauthenticationservice.dtos.ValidateTokenDto;
import com.shubham.userauthenticationservice.exceptions.PasswordMismatchException;
import com.shubham.userauthenticationservice.exceptions.UnAuthorizedException;
import com.shubham.userauthenticationservice.exceptions.UserAlreadyExistsException;
import com.shubham.userauthenticationservice.exceptions.UserNotFoundException;
import com.shubham.userauthenticationservice.models.User;
import com.shubham.userauthenticationservice.service.IAuthService;
import com.sun.net.httpserver.HttpsServer;
import org.antlr.v4.runtime.misc.Pair;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignupRequest signupRequest){
        try {
            User user = authService.signUp(signupRequest.getEmail(), signupRequest.getPassword());
            return new ResponseEntity<>(from(user), HttpStatus.CREATED);
        }catch (UserAlreadyExistsException exception){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequest loginRequest){
        try {
            Pair<User, String> response = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add(HttpHeaders.SET_COOKIE,response.b);
            return new ResponseEntity<>(from(response.a), headers, HttpStatus.OK);
        }catch (UserNotFoundException exception){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }catch (PasswordMismatchException exception){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/validateToken")
    public Boolean validateToken(@RequestBody ValidateTokenDto validateTokenDto) throws UnAuthorizedException {
        Boolean result = authService.validateToken(validateTokenDto.getToken(), validateTokenDto.getUserId());

        if(!result){
            throw new UnAuthorizedException("Invalid token, please try again to login");
        }

        return result;
    }

    private UserDto from(User user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRoles());
        return userDto;
    }
}
