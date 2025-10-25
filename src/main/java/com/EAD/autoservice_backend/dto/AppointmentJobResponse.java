package com.EAD.autoservice_backend.dto;


import lombok.Value;

import java.math.BigDecimal;

@Value
public class AppointmentJobResponse {

    private final Long appointmentJobId;
    private final String description;
    private final BigDecimal additional_cost;
    private final String jobStatus;

    private final VehicleResponse vehicle;
    private final ServiceItemResponse serviceItem;

}
