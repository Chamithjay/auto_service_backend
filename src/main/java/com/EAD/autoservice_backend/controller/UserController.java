package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.InitialPasswordResetRequest;
import com.EAD.autoservice_backend.dto.MessageResponse;
import com.EAD.autoservice_backend.model.User;
import com.EAD.autoservice_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for user management.
 * Handles password reset operations for employees.
 */
@RestController
@RequestMapping("/api/v1/employees")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Forces a password reset for the currently authenticated user.
     *
     * @param request the password reset request containing the new password
     * @return ResponseEntity containing a success message
     */
    @PostMapping("/me/force-reset-password")
    public ResponseEntity<MessageResponse> forceResetPassword(@Valid @RequestBody InitialPasswordResetRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.forceResetPassword(user.getId(), request.getNewPassword());
        return ResponseEntity.ok(new MessageResponse("Password has been reset successfully."));
    }
}
