package com.EAD.autoservice_backend.dto;

/**
 * DTO for customer dashboard statistics
 */
public class CustomerDashboardStatsResponse {
    
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

    // Getters and Setters
    public Integer getTotalVehicles() { return totalVehicles; }
    public void setTotalVehicles(Integer totalVehicles) { this.totalVehicles = totalVehicles; }

    public Integer getTotalAppointments() { return totalAppointments; }
    public void setTotalAppointments(Integer totalAppointments) { this.totalAppointments = totalAppointments; }

    public Integer getActiveAppointments() { return activeAppointments; }
    public void setActiveAppointments(Integer activeAppointments) { this.activeAppointments = activeAppointments; }

    public Integer getCompletedAppointments() { return completedAppointments; }
    public void setCompletedAppointments(Integer completedAppointments) { this.completedAppointments = completedAppointments; }
}
