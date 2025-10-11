package com.example.msauth.dto.response;

import com.example.msauth.model.enums.Role;
import com.example.msauth.model.enums.UserStatus;
import lombok.Builder;
import lombok.Data;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchConnectionDetails;

@Data
@Builder
public class UserResponse {
   private String username;
   private String password;
   private String email;
   private Role role;
   private UserStatus status;
}
