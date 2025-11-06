package com.EAD.autoservice_backend.repository;

import com.EAD.autoservice_backend.model.PasswordResetOTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetOTPRepository extends JpaRepository<PasswordResetOTP, Long> {
    Optional<PasswordResetOTP> findByEmailAndOtpAndIsUsedFalse(String email, String otp);
    void deleteByEmail(String email);
}
