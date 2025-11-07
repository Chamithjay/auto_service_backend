package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.LoginRequest;
import com.EAD.autoservice_backend.dto.LoginResponse;
import com.EAD.autoservice_backend.dto.RegisterRequest;
import com.EAD.autoservice_backend.dto.RegisterResponse;
import com.EAD.autoservice_backend.exception.UserAlreadyExistsException;
import com.EAD.autoservice_backend.model.Customer;
import com.EAD.autoservice_backend.model.Role;
import com.EAD.autoservice_backend.repository.UserRepository;
import com.EAD.autoservice_backend.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuthServiceIT {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Test
    void testRegisterUser_Success() {
        RegisterRequest request = new RegisterRequest("john", "john@example.com", "pass123", "1234567890");

        RegisterResponse response = authService.registerUser(request);

        assertEquals("User registered successfully. Please login to continue.", response.getMessage());
        assertEquals("john", response.getUsername());
        assertEquals("john@example.com", response.getEmail());

        Customer saved = (Customer) userRepository.findByUsername("john").orElseThrow();
        assertTrue(passwordEncoder.matches("pass123", saved.getPassword()));
        assertEquals(Role.CUSTOMER, saved.getRole());
    }

    @Test
    void testRegisterUser_UsernameExists_Throws() {
        Customer existing = new Customer();
        existing.setUsername("alice");
        existing.setEmail("alice@x.com");
        existing.setPassword("pw");
        userRepository.save(existing);

        RegisterRequest request = new RegisterRequest("alice", "alice2@x.com", "pw", "0987654321");
        assertThrows(UserAlreadyExistsException.class, () -> authService.registerUser(request));
    }

    @Test
    void testLoginUser_Success() {
        // Register first
        RegisterRequest reg = new RegisterRequest("bob", "bob@x.com", "pass123", "111222333");
        authService.registerUser(reg);

        // Login
        LoginRequest loginReq = new LoginRequest("bob", "pass123");
        LoginResponse loginResp = authService.loginUser(loginReq);

        assertEquals("bob", loginResp.getUsername());
        assertEquals("bob@x.com", loginResp.getEmail());
        assertEquals("CUSTOMER", loginResp.getRole());
        assertNotNull(loginResp.getToken());
        assertTrue(loginResp.isRequiresPasswordChange());
    }

    @Test
    void testLoginUser_WrongPassword_Throws() {
        RegisterRequest reg = new RegisterRequest("carol", "carol@x.com", "pass123", "111222333");
        authService.registerUser(reg);

        LoginRequest loginReq = new LoginRequest("carol", "wrongpass");
        assertThrows(Exception.class, () -> authService.loginUser(loginReq));
    }
}
