package com.EAD.autoservice_backend.dto;

import java.math.BigDecimal;

// A 'record' is the best way to create a simple DTO.
// It automatically creates the fields, constructor, and getters.
public record ServiceItemRequest(
        String serviceItemName,
        String vehicleType, // e.g., "CAR", "VAN"
        Integer requiredEmployeeCount,
        BigDecimal serviceItemCost,
        String serviceItemType, // e.g., "SERVICE", "MODIFICATION"
        Integer estimatedDuration
) {}