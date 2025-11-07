package com.EAD.autoservice_backend.dto;

import lombok.*;
import com.EAD.autoservice_backend.model.VehicleType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponse {
    private Long vehicleId;
    private String vehicleName;
    private VehicleType vehicleType;
}
