package com.EAD.autoservice_backend.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for customer profile response
 */
@Setter
@Getter
public class CustomerProfileResponse {
    // Getters and Setters
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

}