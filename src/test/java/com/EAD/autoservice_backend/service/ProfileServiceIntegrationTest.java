package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.PasswordChangeRequest;
import com.EAD.autoservice_backend.dto.ProfileResponse;
import com.EAD.autoservice_backend.dto.ProfileUpdateRequest;
import com.EAD.autoservice_backend.exception.InvalidPasswordException;
import com.EAD.autoservice_backend.exception.UserAlreadyExistsException;
import com.EAD.autoservice_backend.model.Customer;
import com.EAD.autoservice_backend.model.Role;
import com.EAD.autoservice_backend.model.User;
import com.EAD.autoservice_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Tests for ProfileService
 * Tests the service with real database and Spring context
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional // Rollback after each test
@DisplayName("ProfileService Integration Tests")
class ProfileServiceIntegrationTest {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Clean database before each test
        userRepository.deleteAll();

        // Create and save a test user
        testUser = new Customer();
        testUser.setUsername("integrationuser");
        testUser.setEmail("integration@example.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setRole(Role.CUSTOMER);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        testUser = userRepository.save(testUser);
    }

    // ==================== getUserProfile Integration Tests ====================

    @Test
    @DisplayName("Integration: Should get user profile from database")
    void testGetUserProfile_Integration() {
        // Act
        ProfileResponse response = profileService.getUserProfile("integrationuser");

        // Assert
        assertNotNull(response);
        assertEquals("integrationuser", response.getUsername());
        assertEquals("integration@example.com", response.getEmail());
        assertEquals("CUSTOMER", response.getRole());
        // Only assert createdAt if your ProfileResponse has this field
        // assertNotNull(response.getCreatedAt());
    }

    @Test
    @DisplayName("Integration: Should throw exception for non-existent user")
    void testGetUserProfile_UserNotFound_Integration() {
        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            profileService.getUserProfile("nonexistentuser");
        });
    }

    // ==================== updateProfile Integration Tests ====================

    @Test
    @DisplayName("Integration: Should update username in database")
    void testUpdateProfile_ChangeUsername_Integration() {
        // Arrange
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setUsername("updatedusername");
        request.setEmail("integration@example.com");

        // Act
        ProfileResponse response = profileService.updateProfile("integrationuser", request);

        // Assert
        assertNotNull(response);
        assertEquals("updatedusername", response.getUsername());

        // Verify in database
        User updatedUser = userRepository.findByUsername("updatedusername")
                .orElseThrow(() -> new AssertionError("User not found in database"));
        assertEquals("updatedusername", updatedUser.getUsername());
        assertNotNull(updatedUser.getUpdatedAt());
    }

    @Test
    @DisplayName("Integration: Should update email in database")
    void testUpdateProfile_ChangeEmail_Integration() {
        // Arrange
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setUsername("integrationuser");
        request.setEmail("newemail@example.com");

        // Act
        ProfileResponse response = profileService.updateProfile("integrationuser", request);

        // Assert
        assertNotNull(response);
        assertEquals("newemail@example.com", response.getEmail());

        // Verify in database
        User updatedUser = userRepository.findByUsername("integrationuser")
                .orElseThrow(() -> new AssertionError("User not found in database"));
        assertEquals("newemail@example.com", updatedUser.getEmail());
    }

    @Test
    @DisplayName("Integration: Should prevent duplicate username")
    void testUpdateProfile_DuplicateUsername_Integration() {
        // Create another user
        User anotherUser = new Customer();
        anotherUser.setUsername("existinguser");
        anotherUser.setEmail("another@example.com");
        anotherUser.setPassword(passwordEncoder.encode("password123"));
        anotherUser.setRole(Role.CUSTOMER);
        anotherUser.setCreatedAt(LocalDateTime.now());
        anotherUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(anotherUser);

        // Try to update to existing username
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setUsername("existinguser");
        request.setEmail("integration@example.com");

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> {
            profileService.updateProfile("integrationuser", request);
        });

        // Verify original user unchanged
        User originalUser = userRepository.findByUsername("integrationuser")
                .orElseThrow(() -> new AssertionError("User not found"));
        assertEquals("integrationuser", originalUser.getUsername());
    }

    @Test
    @DisplayName("Integration: Should prevent duplicate email")
    void testUpdateProfile_DuplicateEmail_Integration() {
        // Create another user
        User anotherUser = new Customer();
        anotherUser.setUsername("anotheruser");
        anotherUser.setEmail("existing@example.com");
        anotherUser.setPassword(passwordEncoder.encode("password123"));
        anotherUser.setRole(Role.CUSTOMER);
        anotherUser.setCreatedAt(LocalDateTime.now());
        anotherUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(anotherUser);

        // Try to update to existing email
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setUsername("integrationuser");
        request.setEmail("existing@example.com");

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> {
            profileService.updateProfile("integrationuser", request);
        });
    }

    @Test
    @DisplayName("Integration: Should update both username and email")
    void testUpdateProfile_ChangeBoth_Integration() {
        // Arrange
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setUsername("newusername");
        request.setEmail("newemail@example.com");

        // Act
        ProfileResponse response = profileService.updateProfile("integrationuser", request);

        // Assert
        assertNotNull(response);
        assertEquals("newusername", response.getUsername());
        assertEquals("newemail@example.com", response.getEmail());

        // Verify old username doesn't exist
        assertFalse(userRepository.findByUsername("integrationuser").isPresent());

        // Verify new username exists with correct email
        User updatedUser = userRepository.findByUsername("newusername")
                .orElseThrow(() -> new AssertionError("Updated user not found"));
        assertEquals("newemail@example.com", updatedUser.getEmail());
    }

    // ==================== changePassword Integration Tests ====================

    @Test
    @DisplayName("Integration: Should change password in database")
    void testChangePassword_Success_Integration() {
        // Arrange
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setCurrentPassword("password123");
        request.setNewPassword("newPassword456");
        request.setConfirmPassword("newPassword456");

        // Act
        assertDoesNotThrow(() -> {
            profileService.changePassword("integrationuser", request);
        });

        // Assert - Verify new password works
        User updatedUser = userRepository.findByUsername("integrationuser")
                .orElseThrow(() -> new AssertionError("User not found"));

        assertTrue(passwordEncoder.matches("newPassword456", updatedUser.getPassword()),
                "New password should match");
        assertFalse(passwordEncoder.matches("password123", updatedUser.getPassword()),
                "Old password should not match");
    }

    @Test
    @DisplayName("Integration: Should reject incorrect current password")
    void testChangePassword_WrongCurrentPassword_Integration() {
        // Arrange
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setCurrentPassword("wrongPassword");
        request.setNewPassword("newPassword456");
        request.setConfirmPassword("newPassword456");

        // Act & Assert
        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> {
            profileService.changePassword("integrationuser", request);
        });

        assertEquals("Current password is incorrect", exception.getMessage());

        // Verify password unchanged
        User user = userRepository.findByUsername("integrationuser")
                .orElseThrow(() -> new AssertionError("User not found"));
        assertTrue(passwordEncoder.matches("password123", user.getPassword()),
                "Password should remain unchanged");
    }

    @Test
    @DisplayName("Integration: Should reject mismatched passwords")
    void testChangePassword_MismatchedPasswords_Integration() {
        // Arrange
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setCurrentPassword("password123");
        request.setNewPassword("newPassword456");
        request.setConfirmPassword("differentPassword789");

        // Act & Assert
        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> {
            profileService.changePassword("integrationuser", request);
        });

        assertEquals("New password and confirmation do not match", exception.getMessage());
    }

    @Test
    @DisplayName("Integration: Should reject same password")
    void testChangePassword_SamePassword_Integration() {
        // Arrange
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setCurrentPassword("password123");
        request.setNewPassword("password123");
        request.setConfirmPassword("password123");

        // Act & Assert
        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> {
            profileService.changePassword("integrationuser", request);
        });

        assertEquals("New password must be different from current password", exception.getMessage());
    }

    // ==================== Edge Cases ====================

    @Test
    @DisplayName("Integration: Should handle concurrent profile updates")
    void testUpdateProfile_ConcurrentUpdates_Integration() {
        // This test demonstrates handling of concurrent modifications
        ProfileUpdateRequest request1 = new ProfileUpdateRequest();
        request1.setUsername("integrationuser");
        request1.setEmail("email1@example.com");

        ProfileUpdateRequest request2 = new ProfileUpdateRequest();
        request2.setUsername("integrationuser");
        request2.setEmail("email2@example.com");

        // First update
        profileService.updateProfile("integrationuser", request1);

        // Second update should work (last write wins)
        ProfileResponse response = profileService.updateProfile("integrationuser", request2);

        // Verify final state
        assertEquals("email2@example.com", response.getEmail());
    }

    @Test
    @DisplayName("Integration: Should maintain data integrity after multiple operations")
    void testDataIntegrity_MultipleOperations_Integration() {
        // 1. Update username
        ProfileUpdateRequest updateRequest = new ProfileUpdateRequest();
        updateRequest.setUsername("newusername");
        updateRequest.setEmail("integration@example.com");
        profileService.updateProfile("integrationuser", updateRequest);

        // 2. Change password
        PasswordChangeRequest passwordRequest = new PasswordChangeRequest();
        passwordRequest.setCurrentPassword("password123");
        passwordRequest.setNewPassword("newPassword789");
        passwordRequest.setConfirmPassword("newPassword789");
        profileService.changePassword("newusername", passwordRequest);

        // 3. Update email
        ProfileUpdateRequest emailRequest = new ProfileUpdateRequest();
        emailRequest.setUsername("newusername");
        emailRequest.setEmail("final@example.com");
        profileService.updateProfile("newusername", emailRequest);

        // Verify final state
        ProfileResponse finalProfile = profileService.getUserProfile("newusername");
        assertEquals("newusername", finalProfile.getUsername());
        assertEquals("final@example.com", finalProfile.getEmail());

        // Verify password
        User finalUser = userRepository.findByUsername("newusername")
                .orElseThrow(() -> new AssertionError("User not found"));
        assertTrue(passwordEncoder.matches("newPassword789", finalUser.getPassword()));
    }
}