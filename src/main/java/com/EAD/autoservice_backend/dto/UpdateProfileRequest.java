package com.EAD.autoservice_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for updating customer profile
 */
@Setter
@Getter
public class UpdateProfileRequest {

    // Getters and Setters
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Email(message = "Email should be valid")
    private String email;

    @Size(min = 9, max = 20, message = "Phone number must be between 9 and 20 characters")
    private String phoneNumber;

    public UpdateProfileRequest() {}

    public UpdateProfileRequest(String username, String email, String phoneNumber) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

}