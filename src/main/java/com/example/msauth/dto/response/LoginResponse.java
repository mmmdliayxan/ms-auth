package com.example.msauth.dto.response;

import lombok.Data;

@Data
public class LoginResponse {
    private String username;
    private String token;
}
