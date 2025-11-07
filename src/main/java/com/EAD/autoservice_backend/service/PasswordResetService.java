package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.model.PasswordResetOTP;
import com.EAD.autoservice_backend.model.User;
import com.EAD.autoservice_backend.repository.PasswordResetOTPRepository;
import com.EAD.autoservice_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

/**
 * Service class for handling password reset operations.
 * Manages OTP generation, verification, and password reset functionality.
 */
@Service
@Transactional
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetOTPRepository otpRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${otp.expiry.minutes:15}")
    private int otpExpiryMinutes;

    /**
     * Initiates a password reset process by generating and sending an OTP to the user's email.
     * Deletes any existing OTPs for the email before creating a new one.
     *
     * @param email the user's email address
     * @throws RuntimeException if user with the email is not found
     */
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with this email not found"));

        String otp = String.format("%06d", new Random().nextInt(999999));

        otpRepository.deleteByEmail(email);

        PasswordResetOTP resetOTP = new PasswordResetOTP(email, otp, otpExpiryMinutes);
        otpRepository.save(resetOTP);

        emailService.sendOTPEmail(email, otp);
    }

    /**
     * Verifies if an OTP is valid for the given email.
     * Checks if the OTP exists, is not used, and has not expired.
     *
     * @param email the user's email address
     * @param otp the OTP to verify
     * @return true if OTP is valid, false otherwise
     */
    public boolean verifyOTP(String email, String otp) {
        Optional<PasswordResetOTP> resetOTPOpt = otpRepository
                .findByEmailAndOtpAndIsUsedFalse(email, otp);

        if (resetOTPOpt.isEmpty()) {
            return false;
        }

        PasswordResetOTP resetOTP = resetOTPOpt.get();
        return !resetOTP.isExpired();
    }

    /**
     * Resets the user's password after verifying the OTP.
     * Marks the OTP as used after successful password reset.
     *
     * @param email the user's email address
     * @param otp the OTP to verify
     * @param newPassword the new password to set
     * @throws RuntimeException if OTP is invalid, expired, or user is not found
     */
    public void resetPassword(String email, String otp, String newPassword) {
        PasswordResetOTP resetOTP = otpRepository
                .findByEmailAndOtpAndIsUsedFalse(email, otp)
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));

        if (resetOTP.isExpired()) {
            throw new RuntimeException("OTP has expired");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        resetOTP.setUsed(true);
        otpRepository.save(resetOTP);
    }
}
