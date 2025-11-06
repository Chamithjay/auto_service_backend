package com.EAD.autoservice_backend.dto;


import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
public class EmployeeAppointmentJobResponse {

    private final Long appointmentJobId;
    private final String jonNote;
    private final BigDecimal additional_cost;
    private final String jobStatus;

    private final EmployeeVehicleResponse vehicle;
    private final EmployeeServiceItemResponse serviceItem;
    private final EmployeeCustomerDetailsResponse customer;

    private final List<EmployeeJobAssignmentResponse> jobAssignments;

}
