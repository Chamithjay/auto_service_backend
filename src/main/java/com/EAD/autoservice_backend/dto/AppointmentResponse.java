package com.EAD.autoservice_backend.dto;

import com.EAD.autoservice_backend.model.SessionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponse {
    private Long appointmentId;
    private String vehicleName;
    private LocalDate appointmentDate;
    private SessionType sessionType;             // 🆕 Added
    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal totalCost;
    private Integer totalApproximatedDuration;   // 🆕 Added
    private String status;
    private String message;                      // 🆕 Added
    private List<ServiceItemDTO> selectedItems;
}
