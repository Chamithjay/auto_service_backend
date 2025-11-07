package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.exception.BadRequestException;
import com.EAD.autoservice_backend.exception.ResourceNotFoundException;
import com.EAD.autoservice_backend.model.User;
import com.EAD.autoservice_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.EAD.autoservice_backend.dto.UserDTO;

import java.util.List;

/**
 * Service class for user management operations.
 * Handles password resets and user information retrieval.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a UserService with the required dependencies.
     *
     * @param userRepository repository for user operations
     * @param passwordEncoder encoder for password hashing
     */
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Forces a password reset for a user and marks password change as not required.
     *
     * @param userId the user ID
     * @param newPassword the new password to set
     * @throws ResourceNotFoundException if user is not found
     * @throws BadRequestException if new password is empty or blank
     */
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

    /**
     * Retrieves basic information for all customers.
     *
     * @return list of customer basic information DTOs
     */
    public List<UserDTO> getAllCustomersBasicInfo() {
        return userRepository.findAllCustomersBasicInfo();
    }

}
