package com.EAD.autoservice_backend.dto;

import com.EAD.autoservice_backend.model.VehicleType;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for vehicle response
 */
@Setter
@Getter
public class CustomerVehicleResponse {

    // Getters and Setters
    private Long vehicleId;
    private String vehicleName;
    private String registrationNo;
    private VehicleType vehicleType;
    private String model;
    private String createdAt;
    private String updatedAt;

    public CustomerVehicleResponse() {}

    public CustomerVehicleResponse(Long vehicleId, String vehicleName, String registrationNo,
                           VehicleType vehicleType, String model, String createdAt, String updatedAt) {
        this.vehicleId = vehicleId;
        this.vehicleName = vehicleName;
        this.registrationNo = registrationNo;
        this.vehicleType = vehicleType;
        this.model = model;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}