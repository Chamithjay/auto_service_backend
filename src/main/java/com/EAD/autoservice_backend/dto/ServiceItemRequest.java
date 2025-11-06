package com.EAD.autoservice_backend.dto;

import java.math.BigDecimal;


public record ServiceItemRequest(
        String serviceItemName,
        String vehicleType, // e.g., "CAR", "VAN"
        Integer requiredEmployeeCount,
        BigDecimal serviceItemCost,
        String serviceItemType, // e.g., "SERVICE", "MODIFICATION"
        Integer estimatedDuration
) {}