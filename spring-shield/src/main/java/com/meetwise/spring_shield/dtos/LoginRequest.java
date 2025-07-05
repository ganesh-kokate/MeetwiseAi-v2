package com.meetwise.spring_shield.dtos;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
