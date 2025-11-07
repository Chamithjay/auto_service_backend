package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.ForgotPasswordRequest;
import com.EAD.autoservice_backend.dto.ResetPasswordRequest;
import com.EAD.autoservice_backend.dto.VerifyOTPRequest;
import com.EAD.autoservice_backend.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for password reset operations.
 * Handles forgot password, OTP verification, and password reset functionality.
 */
@RestController
@RequestMapping("/api/v1/auth/password")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    /**
     * Initiates password reset process by sending OTP to user's email.
     *
     * @param request the forgot password request containing user's email
     * @return ResponseEntity containing success message or error
     */
    @PostMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            passwordResetService.initiatePasswordReset(request.getEmail());
            return ResponseEntity.ok(Map.of("message", "OTP sent to your email"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Verifies the OTP sent to user's email.
     *
     * @param request the OTP verification request containing email and OTP
     * @return ResponseEntity containing verification result
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOTP(@RequestBody VerifyOTPRequest request) {
        boolean isValid = passwordResetService.verifyOTP(
                request.getEmail(),
                request.getOtp()
        );

        if (isValid) {
            return ResponseEntity.ok(Map.of("message", "OTP verified successfully"));
        } else {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid or expired OTP"));
        }
    }

    /**
     * Resets the user's password after OTP verification.
     *
     * @param request the password reset request containing email, OTP, and new password
     * @return ResponseEntity containing success message or error
     */
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            passwordResetService.resetPassword(
                    request.getEmail(),
                    request.getOtp(),
                    request.getNewPassword()
            );
            return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
