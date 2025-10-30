package com.EAD.autoservice_backend.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentCreateRequest {
    private Long vehicleId;
    private List<Long> selectedServiceItemIds;
    private LocalDate appointmentDate;
    private LocalTime startTime;
}
