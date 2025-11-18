package com.EAD.autoservice_backend.dto;

import com.EAD.autoservice_backend.model.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for creating or updating a vehicle
 */
@Setter
@Getter
public class VehicleRequest {

    // Getters and Setters
    @NotBlank(message = "Vehicle name is required")
    @Size(max = 255, message = "Vehicle name must not exceed 255 characters")
    private String vehicleName;

    @NotBlank(message = "Registration number is required")
    @Size(max = 255, message = "Registration number must not exceed 255 characters")
    private String registrationNo;

    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    @Size(max = 255, message = "Model must not exceed 255 characters")
    private String model;

    public VehicleRequest() {}

    public VehicleRequest(String vehicleName, String registrationNo, VehicleType vehicleType, String model) {
        this.vehicleName = vehicleName;
        this.registrationNo = registrationNo;
        this.vehicleType = vehicleType;
        this.model = model;
    }

}