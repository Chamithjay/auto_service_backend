package com.EAD.autoservice_backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class DashboardStatsResponse {
    // Getters and Setters
    private Long totalUsers;
    private Long totalAppointments;
    private Long totalEmployees;
    private Long totalVehicles;
    private BigDecimal totalRevenue;

    public DashboardStatsResponse() {}

    public DashboardStatsResponse(Long totalUsers, Long totalAppointments, Long totalEmployees,
                                  Long totalVehicles, BigDecimal totalRevenue) {
        this.totalUsers = totalUsers;
        this.totalAppointments = totalAppointments;
        this.totalEmployees = totalEmployees;
        this.totalVehicles = totalVehicles;
        this.totalRevenue = totalRevenue;
    }

}