package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.model.PasswordResetOTP;
import com.EAD.autoservice_backend.model.User;
import com.EAD.autoservice_backend.repository.PasswordResetOTPRepository;
import com.EAD.autoservice_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordResetOTPRepository otpRepository;
    @Mock private EmailService emailService;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @BeforeEach
    void setUp() throws Exception {
        // Set the private field otpExpiryMinutes for predictable behavior
        Field f = PasswordResetService.class.getDeclaredField("otpExpiryMinutes");
        f.setAccessible(true);
        f.setInt(passwordResetService, 10);
    }

    @Test
    void initiatePasswordReset_success() {
        String email = "user@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mock(User.class)));
        when(otpRepository.save(any(PasswordResetOTP.class))).thenAnswer(inv -> inv.getArgument(0));

        passwordResetService.initiatePasswordReset(email);

        verify(otpRepository).deleteByEmail(email);

        ArgumentCaptor<String> otpCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService).sendOTPEmail(eq(email), otpCaptor.capture());
        String sentOtp = otpCaptor.getValue();
        assertNotNull(sentOtp);
        assertTrue(sentOtp.matches("\\d{6}"), "OTP should be 6 digits");
    }

    @Test
    void initiatePasswordReset_userNotFound_throws() {
        String email = "missing@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> passwordResetService.initiatePasswordReset(email));
        assertEquals("User with this email not found", ex.getMessage());

        verify(emailService, never()).sendOTPEmail(anyString(), anyString());
        verify(otpRepository, never()).save(any());
    }

    @Test
    void verifyOTP_notFound_returnsFalse() {
        when(otpRepository.findByEmailAndOtpAndIsUsedFalse("e", "o"))
                .thenReturn(Optional.empty());

        assertFalse(passwordResetService.verifyOTP("e", "o"));
    }

    @Test
    void verifyOTP_found_notExpired_returnsTrue() {
        PasswordResetOTP otp = mock(PasswordResetOTP.class);
        when(otp.isExpired()).thenReturn(false);
        when(otpRepository.findByEmailAndOtpAndIsUsedFalse("e", "o"))
                .thenReturn(Optional.of(otp));

        assertTrue(passwordResetService.verifyOTP("e", "o"));
    }

    @Test
    void verifyOTP_found_expired_returnsFalse() {
        PasswordResetOTP otp = mock(PasswordResetOTP.class);
        when(otp.isExpired()).thenReturn(true);
        when(otpRepository.findByEmailAndOtpAndIsUsedFalse("e", "o"))
                .thenReturn(Optional.of(otp));

        assertFalse(passwordResetService.verifyOTP("e", "o"));
    }

    @Test
    void resetPassword_success() {
        String email = "user@example.com";
        String otpValue = "123456";
        String newPassword = "newPw";

        PasswordResetOTP otp = mock(PasswordResetOTP.class);
        when(otp.isExpired()).thenReturn(false);
        when(otpRepository.findByEmailAndOtpAndIsUsedFalse(email, otpValue))
                .thenReturn(Optional.of(otp));

        User user = mock(User.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn("hashed");

        passwordResetService.resetPassword(email, otpValue, newPassword);

        verify(passwordEncoder).encode(newPassword);
        verify(user).setPassword("hashed");
        verify(user).setUpdatedAt(any(LocalDateTime.class));
        verify(userRepository).save(user);

        verify(otp).setUsed(true);
        verify(otpRepository).save(otp);
    }

    @Test
    void resetPassword_invalidOtp_throws() {
        when(otpRepository.findByEmailAndOtpAndIsUsedFalse("e", "o"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> passwordResetService.resetPassword("e", "o", "np"));
        assertEquals("Invalid OTP", ex.getMessage());

        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any());
        verify(otpRepository, never()).save(any());
    }

    @Test
    void resetPassword_expiredOtp_throws() {
        PasswordResetOTP otp = mock(PasswordResetOTP.class);
        when(otp.isExpired()).thenReturn(true);
        when(otpRepository.findByEmailAndOtpAndIsUsedFalse("e", "o"))
                .thenReturn(Optional.of(otp));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> passwordResetService.resetPassword("e", "o", "np"));
        assertEquals("OTP has expired", ex.getMessage());

        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any());
        verify(otpRepository, never()).save(any());
    }

    @Test
    void resetPassword_userNotFound_throws() {
        String email = "missing@example.com";
        PasswordResetOTP otp = mock(PasswordResetOTP.class);
        when(otp.isExpired()).thenReturn(false);
        when(otpRepository.findByEmailAndOtpAndIsUsedFalse(email, "o"))
                .thenReturn(Optional.of(otp));
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> passwordResetService.resetPassword(email, "o", "np"));
        assertEquals("User not found", ex.getMessage());

        verify(userRepository, never()).save(any());
        verify(otpRepository, never()).save(any());
    }
}
