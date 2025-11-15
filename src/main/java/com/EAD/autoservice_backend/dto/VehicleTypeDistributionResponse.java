package com.EAD.autoservice_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VehicleTypeDistributionResponse {
    // Getters and Setters
    private String vehicleType;
    private Long count;

    public VehicleTypeDistributionResponse() {}

    public VehicleTypeDistributionResponse(String vehicleType, Long count) {
        this.vehicleType = vehicleType;
        this.count = count;
    }

}