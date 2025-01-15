package com.shubham.userauthenticationservice.service;

import com.shubham.userauthenticationservice.exceptions.PasswordMismatchException;
import com.shubham.userauthenticationservice.exceptions.UserAlreadyExistsException;
import com.shubham.userauthenticationservice.exceptions.UserNotFoundException;
import com.shubham.userauthenticationservice.models.User;

public interface IAuthService {

    public User signUp(String email, String password) throws UserAlreadyExistsException;

    public User login(String email, String password) throws UserNotFoundException, PasswordMismatchException;
}
