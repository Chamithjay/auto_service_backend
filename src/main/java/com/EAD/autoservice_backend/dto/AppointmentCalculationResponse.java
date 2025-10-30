package com.EAD.autoservice_backend.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentCalculationResponse {
    private BigDecimal totalCost;
    private LocalTime estimatedEndTime;
}
