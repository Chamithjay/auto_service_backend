package com.EAD.autoservice_backend.dto;

import com.EAD.autoservice_backend.model.VehicleType;

/**
 * DTO for vehicle response
 */
public class VehicleResponse {
    
    private Long vehicleId;
    private String vehicleName;
    private String registrationNo;
    private VehicleType vehicleType;
    private String model;
    private String createdAt;
    private String updatedAt;

    public VehicleResponse() {}

    public VehicleResponse(Long vehicleId, String vehicleName, String registrationNo, 
                          VehicleType vehicleType, String model, String createdAt, String updatedAt) {
        this.vehicleId = vehicleId;
        this.vehicleName = vehicleName;
        this.registrationNo = registrationNo;
        this.vehicleType = vehicleType;
        this.model = model;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public String getVehicleName() { return vehicleName; }
    public void setVehicleName(String vehicleName) { this.vehicleName = vehicleName; }

    public String getRegistrationNo() { return registrationNo; }
    public void setRegistrationNo(String registrationNo) { this.registrationNo = registrationNo; }

    public VehicleType getVehicleType() { return vehicleType; }
    public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}