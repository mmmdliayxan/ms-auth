package com.example.msauth;

import com.example.msauth.dto.request.LoginRequest;
import com.example.msauth.dto.request.UserRequest;
import com.example.msauth.dto.response.LoginResponse;
import com.example.msauth.dto.response.UserResponse;
import com.example.msauth.mapper.UserMapper;
import com.example.msauth.model.User;
import com.example.msauth.model.enums.UserStatus;
import com.example.msauth.repository.UserRepository;
import com.example.msauth.service.AuthService;
import com.example.msauth.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtService jwtService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ==========================
    // REGISTER
    // ==========================
    @Test
    void register_shouldReturnUserResponse() {
        UserRequest request = new UserRequest();
        request.setUsername("ayxan");
        request.setPassword("password");

        User userEntity = new User();
        userEntity.setUsername("ayxan");
        userEntity.setPassword("password");

        User savedUser = new User();
        savedUser.setUsername("ayxan");
        savedUser.setPassword("encodedPass");
        savedUser.setStatus(UserStatus.ACTIVE);

        UserResponse response = UserResponse.builder()
                .username("ayxan")
                .status(UserStatus.ACTIVE) // enum olaraq saxla
                .build();

        when(userRepository.findByUsername("ayxan")).thenReturn(Optional.empty());
        when(userMapper.toEntity(request)).thenReturn(userEntity);
        when(passwordEncoder.encode("password")).thenReturn("encodedPass");
        when(userRepository.save(userEntity)).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(response);

        UserResponse result = authService.register(request);

        assertEquals("ayxan", result.getUsername());
        assertEquals(UserStatus.ACTIVE, result.getStatus()); // enum ilə müqayisə
    }


    @Test
    void register_shouldThrow_whenUsernameExists() {
        UserRequest request = new UserRequest();
        request.setUsername("ayxan");

        User existing = new User();
        existing.setUsername("ayxan");

        when(userRepository.findByUsername("ayxan")).thenReturn(Optional.of(existing));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.register(request));
        assertEquals("Username already exists", ex.getMessage());
    }

    // ==========================
    // LOGIN
    // ==========================
    @Test
    void login_shouldReturnResponse_whenPasswordCorrect() {
        LoginRequest request = new LoginRequest();
        request.setUsername("ayxan");
        request.setPassword("password");

        User user = new User();
        user.setUsername("ayxan");
        user.setPassword("encodedPass");

        when(userRepository.findByUsername("ayxan")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPass")).thenReturn(true);
        when(jwtService.issueToken(user)).thenReturn("jwt-token");

        ResponseEntity<?> response = authService.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        LoginResponse body = (LoginResponse) response.getBody();
        assertNotNull(body);
        assertEquals("ayxan", body.getUsername());
        assertEquals("jwt-token", body.getToken());
    }

    @Test
    void login_shouldReturnUnauthorized_whenPasswordIncorrect() {
        LoginRequest request = new LoginRequest();
        request.setUsername("ayxan");
        request.setPassword("wrongpass");

        User user = new User();
        user.setUsername("ayxan");
        user.setPassword("encodedPass");

        when(userRepository.findByUsername("ayxan")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "encodedPass")).thenReturn(false);

        ResponseEntity<?> response = authService.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
