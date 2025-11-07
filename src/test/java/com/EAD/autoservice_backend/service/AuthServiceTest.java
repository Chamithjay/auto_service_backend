package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.LoginRequest;
import com.EAD.autoservice_backend.dto.LoginResponse;
import com.EAD.autoservice_backend.dto.RegisterRequest;
import com.EAD.autoservice_backend.dto.RegisterResponse;
import com.EAD.autoservice_backend.exception.UserAlreadyExistsException;
import com.EAD.autoservice_backend.model.Role;
import com.EAD.autoservice_backend.model.User;
import com.EAD.autoservice_backend.model.Customer;
import com.EAD.autoservice_backend.repository.UserRepository;
import com.EAD.autoservice_backend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserDetailsService userDetailsService;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setup() {
        // no-op
    }

    @Test
    void registerUser_success() {
        RegisterRequest req = mock(RegisterRequest.class);
        when(req.getUsername()).thenReturn("alice");
        when(req.getEmail()).thenReturn("alice@example.com");
        when(req.getPassword()).thenReturn("plain");
        when(req.getPhoneNumber()).thenReturn("1234567890");

        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plain")).thenReturn("hashed");
        when(userRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        RegisterResponse res = authService.registerUser(req);

        assertNotNull(res);
        assertEquals("alice", res.getUsername());
        assertEquals("alice@example.com", res.getEmail());

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(userRepository).save(captor.capture());
        Customer saved = captor.getValue();
        assertEquals("alice", saved.getUsername());
        assertEquals("alice@example.com", saved.getEmail());
        assertEquals("hashed", saved.getPassword());
        assertEquals(Role.CUSTOMER, saved.getRole());
    }

    @Test
    void registerUser_usernameTaken_throws() {
        RegisterRequest req = mock(RegisterRequest.class);
        when(req.getUsername()).thenReturn("alice");

        when(userRepository.existsByUsername("alice")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.registerUser(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_emailTaken_throws() {
        RegisterRequest req = mock(RegisterRequest.class);
        when(req.getUsername()).thenReturn("alice");
        when(req.getEmail()).thenReturn("alice@example.com");

        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.registerUser(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void loginUser_success() {
        LoginRequest req = mock(LoginRequest.class);
        when(req.getUsername()).thenReturn("bob");
        when(req.getPassword()).thenReturn("pw");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));

        // Unnecessary stub removed
        // UserDetails ud = ...
        // when(userDetailsService.loadUserByUsername("bob")).thenReturn(ud);

        when(jwtUtil.generateToken(anyString(), anyLong(), anyString(), anyString()))
                .thenReturn("jwt-token");

        User user = mock(User.class);
        when(user.getUsername()).thenReturn("bob");
        when(user.getEmail()).thenReturn("bob@example.com");
        when(user.getRole()).thenReturn(Role.CUSTOMER);
        when(user.isRequiresPasswordChange()).thenReturn(false);
        when(user.getId()).thenReturn(0L);
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(user));

        LoginResponse res = authService.loginUser(req);

        assertNotNull(res);
        assertEquals("jwt-token", res.getToken());
        assertEquals("bob", res.getUsername());
        assertEquals("bob@example.com", res.getEmail());
        assertEquals("CUSTOMER", res.getRole());
        assertFalse(res.isRequiresPasswordChange());
    }

    @Test
    void loginUser_badCredentials_throwsMaskedMessage() {
        LoginRequest req = mock(LoginRequest.class);
        when(req.getUsername()).thenReturn("bob");
        when(req.getPassword()).thenReturn("wrong");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad creds from provider"));

        BadCredentialsException ex =
                assertThrows(BadCredentialsException.class, () -> authService.loginUser(req));
        assertEquals("Invalid username or password", ex.getMessage());
    }
}
