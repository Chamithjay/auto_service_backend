package com.EAD.autoservice_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class EmployeeJobAssignmentAddCostsRequest {

    @NotNull(message = "Additional cost must not be empty")
    private final BigDecimal additional_cost;
    @NotNull(message = "Cost note is required")
    private final String cost_note;
}
