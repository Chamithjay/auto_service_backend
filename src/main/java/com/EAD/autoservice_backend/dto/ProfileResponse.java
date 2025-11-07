package com.EAD.autoservice_backend.dto;

public class ProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String token; // New JWT token when username is updated

    public ProfileResponse() {}

    public ProfileResponse(Long id, String username, String email, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public ProfileResponse(Long id, String username, String email, String role, String token) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.token = token;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}