package com.EAD.autoservice_backend.dto;

import java.math.BigDecimal;

public class DashboardStatsResponse {
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

    // Getters and Setters
    public Long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(Long totalUsers) { this.totalUsers = totalUsers; }

    public Long getTotalAppointments() { return totalAppointments; }
    public void setTotalAppointments(Long totalAppointments) { this.totalAppointments = totalAppointments; }

    public Long getTotalEmployees() { return totalEmployees; }
    public void setTotalEmployees(Long totalEmployees) { this.totalEmployees = totalEmployees; }

    public Long getTotalVehicles() { return totalVehicles; }
    public void setTotalVehicles(Long totalVehicles) { this.totalVehicles = totalVehicles; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
}