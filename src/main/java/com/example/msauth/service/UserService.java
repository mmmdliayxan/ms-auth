package com.example.msauth.service;

import com.example.msauth.dto.request.UserRequest;
import com.example.msauth.dto.response.UserResponse;
import com.example.msauth.mapper.UserMapper;
import com.example.msauth.model.User;
import com.example.msauth.model.enums.UserStatus;
import com.example.msauth.model.exception.UserNotFoundException;
import com.example.msauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserResponse findById(Long id) {
        log.info("Fetching user by id={}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with id={}", id);
                    return new UserNotFoundException("User not found with id: " + id);
                });
        return userMapper.toResponse(user);
    }

    public Boolean existUser(Long userId) {
        boolean exists = userRepository.existsById(userId);
        log.info("Checking if user with id={} exists: {}", userId, exists);
        return exists;
    }

    public UserResponse getProfile(String email) {
        log.info("Fetching profile for email={}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email={}", email);
                    return new UserNotFoundException("User not found with email: " + email);
                });
        return userMapper.toResponse(user);
    }

    public UserResponse updateProfile(String email, UserRequest request) {
        log.info("Updating profile for email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email={}", email);
                    return new UserNotFoundException("User not found with email: " + email);
                });

        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
            log.debug("Updated username for user={}", email);
        }
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            log.debug("Updated password for user={}", email);
        }

        User updatedUser = userRepository.save(user);
        log.info("User profile updated successfully for email={}", email);
        return userMapper.toResponse(updatedUser);
    }

    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users from database");
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public void deleteUser(Long id) {
        log.warn("Deleting user with id={}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with id={}", id);
                    return new UserNotFoundException("User not found with id: " + id);
                });

        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
        log.info("User with id={} marked as INACTIVE", id);
    }
}
