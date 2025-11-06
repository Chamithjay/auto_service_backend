package com.EAD.autoservice_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Value;

import java.time.LocalDate;

@Value
public class EmployeeLeaveRequest {

    @NotNull(message = "Employee ID cannot be null")
    @Positive(message = "Employee ID must be positive")
    Long employeeId;

    @NotNull(message = "Leave date cannot be null")
    LocalDate leaveDate;

    @NotBlank(message = "Leave type is required")
    @Pattern(regexp = "^(HALFDAY_MORNING|HALFDAY_EVENING|FULLDAY)$", message = "Invalid leave type ")
    private final String leaveType;


    @NotBlank(message = "Leave reason cannot be blank")
    String leaveReason;
}
