package com.EAD.autoservice_backend.dto;

import com.EAD.autoservice_backend.model.Role;

/**
 * DTO for login response sent to React frontend
 * Contains JWT token and user information
 */
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

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isRequiresPasswordChange() { return requiresPasswordChange; }
    public void setRequiresPasswordChange(boolean requiresPasswordChange) { this.requiresPasswordChange = requiresPasswordChange; }
}