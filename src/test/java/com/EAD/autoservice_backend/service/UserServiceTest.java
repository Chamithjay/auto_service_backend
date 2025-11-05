package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.exception.BadRequestException;
import com.EAD.autoservice_backend.exception.ResourceNotFoundException;
import com.EAD.autoservice_backend.model.User;
import com.EAD.autoservice_backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void forceResetPassword_success() {
        // Arrange
        Long userId = 1L;
        User existing = new User();
        existing.setId(userId);
        existing.setPassword("OLD");
        existing.setRequiresPasswordChange(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("newSecret")).thenReturn("ENC(newSecret)");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        userService.forceResetPassword(userId, "newSecret");

        // Assert
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertEquals("ENC(newSecret)", saved.getPassword());
        assertFalse(saved.isRequiresPasswordChange());
        verify(passwordEncoder).encode("newSecret");
    }

    @Test
    void forceResetPassword_userNotFound_throws() {
        when(userRepository.findById(404L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.forceResetPassword(404L, "pw"));
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void forceResetPassword_blankPassword_throws() {
        Long userId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        assertThrows(BadRequestException.class, () -> userService.forceResetPassword(userId, "  "));
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void forceResetPassword_nullPassword_throws() {
        Long userId = 3L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        assertThrows(BadRequestException.class, () -> userService.forceResetPassword(userId, null));
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }
}

