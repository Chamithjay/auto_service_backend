package com.EAD.autoservice_backend.dto;

import com.EAD.autoservice_backend.model.Role;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for login response sent to React frontend
 * Contains JWT token and user information
 */
@Setter
@Getter
public class LoginResponse {
    private String token;
    private String tokenType = "Bearer";
    private String username;
    private String email;
    private String role;
    private boolean requiresPasswordChange;

    public LoginResponse() {}

    public LoginResponse(String token, String username, String email, String role, boolean requiresPasswordChange) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.role = role;
        this.requiresPasswordChange = requiresPasswordChange;
    }

    public LoginResponse(String token, String username, String email, Role role, boolean requiresPasswordChange) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.role = role.name();
        this.requiresPasswordChange = requiresPasswordChange;
    }

}