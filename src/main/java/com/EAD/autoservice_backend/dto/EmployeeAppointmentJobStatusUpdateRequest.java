package com.EAD.autoservice_backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Value;

@Value
public class EmployeeAppointmentJobStatusUpdateRequest {

    @NotNull(message = "Job status must not be empty")
    @Pattern(regexp = "^(NEW|ONGOING|COMPLETED)$", message = "Invalid status ")
    private final String jobStatus;
}
