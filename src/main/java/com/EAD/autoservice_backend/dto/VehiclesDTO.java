package com.EAD.autoservice_backend.dto;

import com.EAD.autoservice_backend.model.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehiclesDTO {
    private Long vehicleId;
    private String vehicleName;
    private String registrationNo;
    private VehicleType vehicleType;
    private String model;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long customerId;

}
