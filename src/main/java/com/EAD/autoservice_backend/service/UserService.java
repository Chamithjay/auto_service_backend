package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.exception.BadRequestException;
import com.EAD.autoservice_backend.exception.ResourceNotFoundException;
import com.EAD.autoservice_backend.model.User;
import com.EAD.autoservice_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void forceResetPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (newPassword == null || newPassword.isBlank()) {
            throw new BadRequestException("New password cannot be empty.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setRequiresPasswordChange(false);
        userRepository.save(user);
    }
}
