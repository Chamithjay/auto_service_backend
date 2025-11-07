package com.EAD.autoservice_backend.dto;

import com.EAD.autoservice_backend.model.AppointmentStatus;
import com.EAD.autoservice_backend.model.SessionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentHistoryResponse {
    private Long appointmentId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm") // ✅ Format as date + time (no seconds, no microseconds)
    private LocalDateTime createdAt;
    private LocalDate appointmentDate;
    private String vehicleName;
    private List<String> selectedServices;
    private SessionType sessionType;
    private AppointmentStatus status;
    private BigDecimal totalCost;
}
