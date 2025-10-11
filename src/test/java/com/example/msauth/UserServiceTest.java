package com.example.msauth;

import com.example.msauth.dto.request.UserRequest;
import com.example.msauth.dto.response.UserResponse;
import com.example.msauth.mapper.UserMapper;
import com.example.msauth.model.User;
import com.example.msauth.model.enums.UserStatus;
import com.example.msauth.repository.UserRepository;
import com.example.msauth.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateProfile_shouldUpdatePasswordAndUsername() {
        User user = new User();
        user.setEmail("old@example.com");
        user.setPassword("oldPass");

        UserRequest request = new UserRequest();
        request.setUsername("newUser");
        request.setPassword("newPass");

        when(userRepository.findByEmail("old@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(UserResponse.builder()
                .username("newUser")
                .status(UserStatus.ACTIVE)
                .build());

        UserResponse response = userService.updateProfile("old@example.com", request);

        assertEquals("newUser", response.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void deleteUser_shouldSetStatusInactive() {
        User user = new User();
        user.setStatus(UserStatus.ACTIVE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.deleteUser(1L);

        assertEquals(UserStatus.INACTIVE, user.getStatus());
        verify(userRepository, times(1)).save(user);
    }
}
