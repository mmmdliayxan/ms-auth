package com.example.msauth.controller;

import com.example.msauth.dto.request.UserRequest;
import com.example.msauth.dto.response.UserResponse;
import com.example.msauth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/byEmail/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<UserResponse> getProfile(@PathVariable String email) {
        log.info("Get profile for email={}", email);
        return ResponseEntity.ok(userService.getProfile(email));
    }

    @GetMapping("/byId/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.info("Received request to get user by id={}", id);
        UserResponse userResponse = userService.findById(id);
        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<UserResponse> updateProfile(@PathVariable String email, @RequestBody UserRequest request) {
        log.info("Update profile for email={}", email);
        return ResponseEntity.ok(userService.updateProfile(email, request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("Get all users");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.warn("Delete user with id={}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Boolean> existsUser(@PathVariable Long id) {
        log.info("Check if user exists with id={}", id);
        return ResponseEntity.ok(userService.existUser(id));
    }
}