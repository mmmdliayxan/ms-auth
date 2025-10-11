package com.example.msauth.mapper;

import com.example.msauth.dto.request.UserRequest;
import com.example.msauth.dto.response.UserResponse;
import com.example.msauth.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequest request);

    UserResponse toResponse(User user);
}
