package com.EAD.autoservice_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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

}