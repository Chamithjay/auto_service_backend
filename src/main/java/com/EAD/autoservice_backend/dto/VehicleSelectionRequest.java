package com.EAD.autoservice_backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleSelectionRequest {
    private Long vehicleId;
}
