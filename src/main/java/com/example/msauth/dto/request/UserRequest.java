package com.example.msauth.dto.request;

import com.example.msauth.model.enums.Role;
import lombok.Data;

@Data
public class UserRequest {
    private String username;
    private String password;
    private String email;
    private Role role;
}
