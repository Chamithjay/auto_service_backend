package com.EAD.autoservice_backend.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for registration response sent to React frontend
 * Contains success message and user information (NO JWT TOKEN)
 */
@Setter
@Getter
public class RegisterResponse {
    private String message;
    private String username;
    private String email;

    // Constructors
    public RegisterResponse() {}

    public RegisterResponse(String message, String username, String email) {
        this.message = message;
        this.username = username;
        this.email = email;
    }

}