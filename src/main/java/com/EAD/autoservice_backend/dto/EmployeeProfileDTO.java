package com.EAD.autoservice_backend.dto;

public record EmployeeProfileDTO(
        String employeeId,
        String username,
        String email,
        String position,
        String department,
        String phoneNumber
) {}
