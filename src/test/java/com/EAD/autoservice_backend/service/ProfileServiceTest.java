package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.PasswordChangeRequest;
import com.EAD.autoservice_backend.dto.ProfileResponse;
import com.EAD.autoservice_backend.dto.ProfileUpdateRequest;
import com.EAD.autoservice_backend.exception.InvalidPasswordException;
import com.EAD.autoservice_backend.exception.UserAlreadyExistsException;
import com.EAD.autoservice_backend.model.Admin;
import com.EAD.autoservice_backend.model.Role;
import com.EAD.autoservice_backend.model.User;
import com.EAD.autoservice_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit Tests for ProfileService
 * Uses Mockito to mock dependencies
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProfileService Unit Tests")
class ProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ProfileService profileService;

    private User testUser;
    private Admin testAdmin;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.CUSTOMER);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        // Create test admin
        testAdmin = new Admin();
        testAdmin.setId(2L);
        testAdmin.setUsername("admin");
        testAdmin.setEmail("admin@example.com");
        testAdmin.setPassword("encodedPassword");
        testAdmin.setRole(Role.ADMIN);
        testAdmin.setCreatedAt(LocalDateTime.now());
        testAdmin.setUpdatedAt(LocalDateTime.now());
    }

    // ==================== getUserProfile Tests ====================

    @Test
    @DisplayName("Should get user profile successfully")
    void testGetUserProfile_Success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        ProfileResponse response = profileService.getUserProfile("testuser");

        // Assert
        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("CUSTOMER", response.getRole());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testGetUserProfile_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            profileService.getUserProfile("nonexistent");
        });
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    // ==================== updateProfile Tests ====================

    @Test
    @DisplayName("Should update profile successfully when changing email")
    void testUpdateProfile_ChangeEmail_Success() {
        // Arrange
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setUsername("testuser");
        request.setEmail("newemail@example.com");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        ProfileResponse response = profileService.updateProfile("testuser", request);

        // Assert
        assertNotNull(response);
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, times(1)).existsByEmail("newemail@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should update profile successfully when changing username")
    void testUpdateProfile_ChangeUsername_Success() {
        // Arrange
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setUsername("newusername");
        request.setEmail("test@example.com");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("newusername")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        ProfileResponse response = profileService.updateProfile("testuser", request);

        // Assert
        assertNotNull(response);
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, times(1)).existsByUsername("newusername");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when new username already exists")
    void testUpdateProfile_UsernameAlreadyExists() {
        // Arrange
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setUsername("existinguser");
        request.setEmail("test@example.com");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> {
            profileService.updateProfile("testuser", request);
        });
        verify(userRepository, times(1)).existsByUsername("existinguser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when new email already exists")
    void testUpdateProfile_EmailAlreadyExists() {
        // Arrange
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setUsername("testuser");
        request.setEmail("existing@example.com");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> {
            profileService.updateProfile("testuser", request);
        });
        verify(userRepository, times(1)).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when user not found during update")
    void testUpdateProfile_UserNotFound() {
        // Arrange
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");

        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            profileService.updateProfile("nonexistent", request);
        });
        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== changePassword Tests ====================

    @Test
    @DisplayName("Should change password successfully")
    void testChangePassword_Success() {
        // Arrange
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setCurrentPassword("oldPassword");
        request.setNewPassword("newPassword123");
        request.setConfirmPassword("newPassword123");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.matches("newPassword123", "encodedPassword")).thenReturn(false);
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        assertDoesNotThrow(() -> {
            profileService.changePassword("testuser", request);
        });

        // Assert
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("oldPassword", "encodedPassword");
        verify(passwordEncoder, times(1)).encode("newPassword123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when current password is incorrect")
    void testChangePassword_IncorrectCurrentPassword() {
        // Arrange
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setCurrentPassword("wrongPassword");
        request.setNewPassword("newPassword123");
        request.setConfirmPassword("newPassword123");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // Act & Assert
        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> {
            profileService.changePassword("testuser", request);
        });
        assertEquals("Current password is incorrect", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when passwords don't match")
    void testChangePassword_PasswordsDoNotMatch() {
        // Arrange
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setCurrentPassword("oldPassword");
        request.setNewPassword("newPassword123");
        request.setConfirmPassword("differentPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);

        // Act & Assert
        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> {
            profileService.changePassword("testuser", request);
        });
        assertEquals("New password and confirmation do not match", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when new password same as current")
    void testChangePassword_NewPasswordSameAsCurrent() {
        // Arrange
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setCurrentPassword("oldPassword");
        request.setNewPassword("oldPassword");
        request.setConfirmPassword("oldPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);

        // Act & Assert
        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> {
            profileService.changePassword("testuser", request);
        });
        assertEquals("New password must be different from current password", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when user not found during password change")
    void testChangePassword_UserNotFound() {
        // Arrange
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setCurrentPassword("oldPassword");
        request.setNewPassword("newPassword123");
        request.setConfirmPassword("newPassword123");

        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            profileService.changePassword("nonexistent", request);
        });
        verify(userRepository, never()).save(any(User.class));
    }
}