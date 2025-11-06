package com.EAD.autoservice_backend.dto;

// No password field here, as this is for updating user info
public record UserUpdateRequest(
        String username,
        String email,
        String role
) {}