package com.EAD.autoservice_backend.dto;

/**
 * DTO for customer profile response
 */
public class CustomerProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String createdAt;
    private String updatedAt;
    private String token; // New JWT token when username is updated

    public CustomerProfileResponse() {}

    public CustomerProfileResponse(Long id, String username, String email, String phoneNumber,
                                   String createdAt, String updatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public CustomerProfileResponse(Long id, String username, String email, String phoneNumber,
                                   String createdAt, String updatedAt, String token) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.token = token;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}