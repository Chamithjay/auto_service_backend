package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.exception.BadRequestException;
import com.EAD.autoservice_backend.exception.ResourceNotFoundException;
import com.EAD.autoservice_backend.model.Admin;
import com.EAD.autoservice_backend.model.Employee;
import com.EAD.autoservice_backend.model.User;
import com.EAD.autoservice_backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> userService.forceResetPassword(404L, "pw"));
        assertEquals("User not found", ex.getMessage());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void forceResetPassword_blankPassword_throws() {
        Long userId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> userService.forceResetPassword(userId, "  "));
        assertTrue(ex.getMessage().contains("cannot be empty"));
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void forceResetPassword_nullPassword_throws() {
        Long userId = 3L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> userService.forceResetPassword(userId, null));
        assertTrue(ex.getMessage().contains("cannot be empty"));
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void forceResetPassword_adminUser_success() {
        Long userId = 5L;
        Admin admin = new Admin();
        admin.setId(userId);
        admin.setUsername("admin");
        admin.setRequiresPasswordChange(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(admin));
        when(passwordEncoder.encode("adminPass")).thenReturn("ENC(adminPass)");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        userService.forceResetPassword(userId, "adminPass");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertEquals("ENC(adminPass)", saved.getPassword());
        assertFalse(saved.isRequiresPasswordChange());
    }

    @Test
    void forceResetPassword_employeeUser_success() {
        Long userId = 6L;
        Employee emp = new Employee();
        emp.setId(userId);
        emp.setUsername("emp");
        emp.setRequiresPasswordChange(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(emp));
        when(passwordEncoder.encode("empPass")).thenReturn("ENC(empPass)");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        userService.forceResetPassword(userId, "empPass");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertEquals("ENC(empPass)", saved.getPassword());
    }

    @Test
    void forceResetPassword_passwordEncoderReceivesRawPassword() {
        Long userId = 7L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("myRawPassword123")).thenReturn("ENCODED_VALUE");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        userService.forceResetPassword(userId, "myRawPassword123");

        ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
        verify(passwordEncoder).encode(passwordCaptor.capture());
        assertEquals("myRawPassword123", passwordCaptor.getValue());
    }

    @Test
    void forceResetPassword_requiresPasswordChangeFlagCleared() {
        Long userId = 8L;
        User user = new User();
        user.setId(userId);
        user.setRequiresPasswordChange(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("ENC");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        userService.forceResetPassword(userId, "pass");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertFalse(captor.getValue().isRequiresPasswordChange());
    }

    @Test
    void forceResetPassword_complexPassword() {
        Long userId = 9L;
        User user = new User();
        user.setId(userId);
        String complexPassword = "P@ssw0rd!#$%^&*()_+-=[]{}|;:',.<>?/";

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(complexPassword)).thenReturn("ENCODED_COMPLEX");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        userService.forceResetPassword(userId, complexPassword);

        ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
        verify(passwordEncoder).encode(passwordCaptor.capture());
        assertEquals(complexPassword, passwordCaptor.getValue());
    }

    @Test
    void forceResetPassword_longPassword() {
        Long userId = 10L;
        User user = new User();
        user.setId(userId);
        String longPassword = "a".repeat(100) + "1234";

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(longPassword)).thenReturn("ENCODED_LONG");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        userService.forceResetPassword(userId, longPassword);

        verify(passwordEncoder).encode(longPassword);
    }

    @Test
    void forceResetPassword_unicodeCharactersInPassword() {
        Long userId = 11L;
        User user = new User();
        user.setId(userId);
        String unicodePassword = "Pässwörd™123™©";

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(unicodePassword)).thenReturn("ENCODED_UNICODE");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        userService.forceResetPassword(userId, unicodePassword);

        ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
        verify(passwordEncoder).encode(passwordCaptor.capture());
        assertEquals(unicodePassword, passwordCaptor.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "   ", "\t", "\n", "\r\n" })
    void forceResetPassword_variousWhitespace_throws(String blankPassword) {
        Long userId = 12L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        assertThrows(BadRequestException.class,
                () -> userService.forceResetPassword(userId, blankPassword));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void forceResetPassword_encodedPasswordPersisted() {
        Long userId = 13L;
        User user = new User();
        user.setId(userId);
        user.setPassword("OLD_PASSWORD");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("rawPassword")).thenReturn("$2a$10$bcryptedHash");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        userService.forceResetPassword(userId, "rawPassword");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("$2a$10$bcryptedHash", captor.getValue().getPassword());
    }

    @Test
    void forceResetPassword_repositoryNotCalledOnValidationFailure() {
        Long userId = 14L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.forceResetPassword(userId, "anyPassword"));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());
    }
}
