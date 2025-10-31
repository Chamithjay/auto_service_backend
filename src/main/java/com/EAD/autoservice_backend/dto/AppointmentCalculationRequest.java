package com.EAD.autoservice_backend.dto;

import com.EAD.autoservice_backend.model.SessionType;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentCalculationRequest {
    private Long vehicleId;
    private List<Long> selectedServiceItemIds;
    private LocalDate appointmentDate;
    private SessionType sessionType;
}
