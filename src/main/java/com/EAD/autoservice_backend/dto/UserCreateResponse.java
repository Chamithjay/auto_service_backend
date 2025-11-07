//UserCreateResponse.java
package com.EAD.autoservice_backend.dto;

// This DTO is for *sending* user data back, so it has no password.
public record UserCreateResponse(
        Long id,
        String username,
        String email,
        String role,
        boolean requiresPasswordChange
) {}