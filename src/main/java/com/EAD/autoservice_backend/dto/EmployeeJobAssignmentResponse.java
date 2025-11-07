package com.EAD.autoservice_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalTime;

@Value
public class EmployeeJobAssignmentResponse {
    @NotNull
    private final Long employeeId;
    @NotNull
    private final String employeeName;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final BigDecimal additionalCost;
    private final String costNote;
}
