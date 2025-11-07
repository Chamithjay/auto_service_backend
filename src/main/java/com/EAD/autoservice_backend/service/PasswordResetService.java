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

    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with this email not found"));

        String otp = String.format("%06d", new Random().nextInt(999999));

        otpRepository.deleteByEmail(email);

        PasswordResetOTP resetOTP = new PasswordResetOTP(email, otp, otpExpiryMinutes);
        otpRepository.save(resetOTP);

        emailService.sendOTPEmail(email, otp);
    }

    public boolean verifyOTP(String email, String otp) {
        Optional<PasswordResetOTP> resetOTPOpt = otpRepository
                .findByEmailAndOtpAndIsUsedFalse(email, otp);

        if (resetOTPOpt.isEmpty()) {
            return false;
        }


        PasswordResetOTP resetOTP = resetOTPOpt.get();
        return !resetOTP.isExpired();
    }

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
