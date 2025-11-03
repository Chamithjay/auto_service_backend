package com.EAD.autoservice_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class EmployeeServiceItemResponse {
    @NotNull
    private final Long serviceItemId;
    @NotNull
    private final String serviceItemName;
    @NotNull
    private final Integer estimatedDuration;
}
