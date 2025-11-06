//UserCreateRequest.java

package com.EAD.autoservice_backend.dto;

public record UserCreateRequest (
    String username,
    String password,
    String email,
    String role // e.g., "EMPLOYEE" or "ADMIN"

){}