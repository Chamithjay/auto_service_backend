package com.EAD.autoservice_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class EmployeeCustomerDetailsResponse {

    @NotNull
    private final String username;
    @NotNull
    private final String phoneNumber;
    @NotNull
    private final String email;
}
