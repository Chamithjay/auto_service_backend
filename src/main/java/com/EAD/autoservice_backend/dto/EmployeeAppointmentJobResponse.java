package com.EAD.autoservice_backend.dto;


import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
public class AppointmentJobResponse {

    private final Long appointmentJobId;
    private final String jonNote;
    private final BigDecimal additional_cost;
    private final String jobStatus;

    private final VehicleResponse vehicle;
    private final ServiceItemResponse serviceItem;
    private final CustomerResponse customer;

    private final List<JobAssignmentResponse> jobAssignments;

}
