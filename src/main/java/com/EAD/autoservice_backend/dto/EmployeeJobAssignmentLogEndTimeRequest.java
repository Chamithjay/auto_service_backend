package com.EAD.autoservice_backend.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.LocalTime;

@Value
public class EmployeeJobAssignmentLogEndTimeRequest {

    @NotNull(message = "End time is required")
    private final LocalTime endTime;

}
