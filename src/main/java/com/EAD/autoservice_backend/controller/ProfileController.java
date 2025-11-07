package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.PasswordChangeRequest;
import com.EAD.autoservice_backend.dto.ProfileResponse;
import com.EAD.autoservice_backend.dto.ProfileUpdateRequest;
import com.EAD.autoservice_backend.exception.InvalidPasswordException;
import com.EAD.autoservice_backend.exception.UserAlreadyExistsException;
import com.EAD.autoservice_backend.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


/**
 * REST controller for user profile management.
 * Handles profile retrieval, updates, and password changes for authenticated users.
 */
@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * Retrieves the current authenticated user's profile.
     *
     * @return ResponseEntity containing the user's profile information
     */
    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        ProfileResponse profile = profileService.getUserProfile(username);
        return ResponseEntity.ok(profile);
    }

    /**
     * Updates the current user's profile information (username and email).
     *
     * @param request the profile update request containing new information
     * @return ResponseEntity containing the updated profile or error message
     */
    @PutMapping
    public ResponseEntity<?> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();

            ProfileResponse updatedProfile = profileService.updateProfile(currentUsername, request);
            return ResponseEntity.ok(updatedProfile);
        } catch (UserAlreadyExistsException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Profile update failed. Please try again.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Changes the current user's password.
     *
     * @param request the password change request containing old and new passwords
     * @return ResponseEntity containing success message or error
     */
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            profileService.changePassword(username, request);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Password changed successfully");
            return ResponseEntity.ok(response);
        } catch (InvalidPasswordException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Password change failed. Please try again.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}


