package com.shubham.userauthenticationservice.service;

import com.shubham.userauthenticationservice.exceptions.PasswordMismatchException;
import com.shubham.userauthenticationservice.exceptions.UserAlreadyExistsException;
import com.shubham.userauthenticationservice.exceptions.UserNotFoundException;
import com.shubham.userauthenticationservice.models.User;
import org.antlr.v4.runtime.misc.Pair;

public interface IAuthService {

    public User signUp(String email, String password) throws UserAlreadyExistsException;

    public Pair<User, String> login(String email, String password) throws UserNotFoundException, PasswordMismatchException;

    public Boolean validateToken(String token, Long userId);
}
