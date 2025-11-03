package com.EAD.autoservice_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Value;

import java.time.LocalTime;

@Value
public class EmployeeJobAssignmentLogStartTimeRequest {

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(NEW|ONGOING|COMPLETED)$", message = "Invalid status ")
    private final String status;

    @NotNull(message = "Start time is required")
    private final LocalTime startTime;

}
