package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.LoginRequest;
import com.EAD.autoservice_backend.dto.LoginResponse;
import com.EAD.autoservice_backend.dto.RegisterRequest;
import com.EAD.autoservice_backend.dto.RegisterResponse;
import com.EAD.autoservice_backend.exception.UserAlreadyExistsException;
import com.EAD.autoservice_backend.model.Customer;
import com.EAD.autoservice_backend.model.Role;
import com.EAD.autoservice_backend.model.User;
import com.EAD.autoservice_backend.model.Customer;
import com.EAD.autoservice_backend.repository.UserRepository;
import com.EAD.autoservice_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service class for handling authentication and user registration.
 * Manages user login, registration, and JWT token generation.
 */
@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    /**
     * Constructs an AuthService with required dependencies.
     *
     * @param userRepository repository for user operations
     * @param passwordEncoder encoder for password hashing
     * @param authenticationManager manager for authentication
     * @param userDetailsService service for loading user details
     * @param jwtUtil utility for JWT token operations
     */
    @Autowired
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       UserDetailsService userDetailsService,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Registers a new user with customer role.
     * Validates username and email uniqueness before creating the user.
     *
     * @param request the registration request containing user details
     * @return registration response with success message and user info
     * @throws UserAlreadyExistsException if username or email already exists
     */
    public RegisterResponse registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username '" + request.getUsername() + "' is already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email '" + request.getEmail() + "' is already registered");
        }

        Customer customer = new Customer();
        customer.setUsername(request.getUsername());
        customer.setEmail(request.getEmail());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        customer.setRole(Role.CUSTOMER);

        User savedUser = userRepository.save(customer);

        return new RegisterResponse(
                "User registered successfully. Please login to continue.",
                savedUser.getUsername(),
                savedUser.getEmail()
        );
    }

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param request the login request containing username and password
     * @return login response with JWT token and user details
     * @throws BadCredentialsException if credentials are invalid
     */
    public LoginResponse loginUser(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new BadCredentialsException("User not found"));

            String token = jwtUtil.generateToken(
                    user.getUsername(),
                    user.getId(),
                    user.getEmail(),
                    user.getRole().name()
            );

            return new LoginResponse(
                    token,
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole().name(),
                    user.isRequiresPasswordChange()
            );

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
