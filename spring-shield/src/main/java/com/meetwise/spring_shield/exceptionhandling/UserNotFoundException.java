package com.meetwise.spring_shield.exceptionhandling;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String username)
    {
        super("User not found with username: " + username);
    }
}

