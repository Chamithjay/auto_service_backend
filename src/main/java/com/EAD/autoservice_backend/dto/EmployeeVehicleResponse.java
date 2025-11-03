package com.EAD.autoservice_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class EmployeeVehicleResponse {
    @NotNull
    private final String registrationNo;
    @NotNull
    private final String vehicleType;
    private final String vehicleModel;
}
