package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.PasswordChangeRequest;
import com.EAD.autoservice_backend.dto.ProfileResponse;
import com.EAD.autoservice_backend.dto.ProfileUpdateRequest;
import com.EAD.autoservice_backend.exception.InvalidPasswordException;
import com.EAD.autoservice_backend.exception.UserAlreadyExistsException;
import com.EAD.autoservice_backend.model.User;
import com.EAD.autoservice_backend.repository.UserRepository;
import com.EAD.autoservice_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public ProfileService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Get user profile by username
     */
    public ProfileResponse getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new ProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    /**
     * Update user profile (username and email)
     */
    public ProfileResponse updateProfile(String currentUsername, ProfileUpdateRequest request) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + currentUsername));

        boolean usernameChanged = false;
        
        // Check if new username is taken by another user
        if (!user.getUsername().equalsIgnoreCase(request.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new UserAlreadyExistsException("Username '" + request.getUsername() + "' is already taken");
            }
            usernameChanged = true;
        }

        // Check if new email is taken by another user
        if (!user.getEmail().equalsIgnoreCase(request.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new UserAlreadyExistsException("Email '" + request.getEmail() + "' is already registered");
            }
        }

        // Update user details
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);

        ProfileResponse response = new ProfileResponse(
                updatedUser.getId(),
                updatedUser.getUsername(),
                updatedUser.getEmail(),
                updatedUser.getRole().name()
        );
        
        // Generate new token if username was changed
        if (usernameChanged) {
            String newToken = jwtUtil.generateToken(
                updatedUser.getUsername(), 
                updatedUser.getId(), 
                updatedUser.getEmail(), 
                updatedUser.getRole().name()
            );
            response.setToken(newToken);
        }
        
        return response;
    }

    /**
     * Change user password
     */
    public void changePassword(String username, PasswordChangeRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }

        // Check if new password matches confirmation
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidPasswordException("New password and confirmation do not match");
        }

        // Check if new password is different from current password
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new InvalidPasswordException("New password must be different from current password");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }
}