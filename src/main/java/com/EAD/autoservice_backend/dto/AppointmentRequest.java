package com.EAD.autoservice_backend.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentRequest {

    private String vehicleName; // sent by frontend
    private List<String> serviceItemName; // Services + Modifications together
    private LocalDate appointmentDate;
    private LocalTime startTime;
}
