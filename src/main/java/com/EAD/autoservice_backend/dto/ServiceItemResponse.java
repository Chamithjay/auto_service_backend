package com.EAD.autoservice_backend.dto;

import com.EAD.autoservice_backend.model.ServiceItemType;
import com.EAD.autoservice_backend.model.VehicleType;

import java.math.BigDecimal;

public record ServiceItemResponse(
        Long serviceItemId,
        String serviceItemName,
        VehicleType vehicleType,
        Integer requiredEmployeeCount,
        BigDecimal serviceItemCost,
        ServiceItemType serviceItemType,
        Integer estimatedDuration
) {}
