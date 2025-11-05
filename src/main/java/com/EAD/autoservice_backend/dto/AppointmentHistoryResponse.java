package com.EAD.autoservice_backend.dto;

import com.EAD.autoservice_backend.model.AppointmentStatus;
import com.EAD.autoservice_backend.model.SessionType;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentHistoryResponse {
    private Long appointmentId;
    private LocalDate appointmentDate;
    private String vehicleName;
    private List<String> selectedServices; // service/modification names
    private SessionType sessionType;
    private AppointmentStatus status;
    private BigDecimal totalCost;
}
