package com.EAD.autoservice_backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class EmployeeJobNoteRequest {
    @NotEmpty(message = "Job note cannot be empty")
    private final String jobNote;
}
