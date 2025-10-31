package com.EAD.autoservice_backend.dto;

import lombok.Value;

@Value
public class VehicleResponse {
    private final String registrationNo;
    private final String vehicleType;
    private final String vehicleModel;
}
