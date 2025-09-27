package com.EAD.autoservice_backend.dto;

// Notice: No password!
public record UserUpdateRequest(
        String username,
        String email,
        String role
) {}