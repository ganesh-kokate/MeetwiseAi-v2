package com.meetwise.spring_shield.dtos;

import com.meetwise.spring_shield.models.Role;
import lombok.Data;

import java.util.Set;

@Data
public class RegisterUserRequest {
    private String username;
    private String password;
    private String mailId;
    private String mobileNumber;
    private Set<String> roles;
}
