package com.EAD.autoservice_backend.dto;

public class VehicleTypeDistributionResponse {
    private String vehicleType;
    private Long count;

    public VehicleTypeDistributionResponse() {}

    public VehicleTypeDistributionResponse(String vehicleType, Long count) {
        this.vehicleType = vehicleType;
        this.count = count;
    }

    // Getters and Setters
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public Long getCount() { return count; }
    public void setCount(Long count) { this.count = count; }
}