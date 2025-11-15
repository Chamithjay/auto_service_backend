package com.EAD.autoservice_backend.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for customer dashboard statistics
 */
@Setter
@Getter
public class CustomerDashboardStatsResponse {

    // Getters and Setters
    private Integer totalVehicles;
    private Integer totalAppointments;
    private Integer activeAppointments;
    private Integer completedAppointments;

    public CustomerDashboardStatsResponse() {}

    public CustomerDashboardStatsResponse(Integer totalVehicles, Integer totalAppointments, 
                                 Integer activeAppointments, Integer completedAppointments) {
        this.totalVehicles = totalVehicles;
        this.totalAppointments = totalAppointments;
        this.activeAppointments = activeAppointments;
        this.completedAppointments = completedAppointments;
    }

}
