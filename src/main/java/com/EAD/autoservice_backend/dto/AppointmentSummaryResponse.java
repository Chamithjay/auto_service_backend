package com.EAD.autoservice_backend.dto;

import com.EAD.autoservice_backend.model.AppointmentStatus;
import java.math.BigDecimal;

/**
 * DTO for appointment summary in customer dashboard
 */
public class AppointmentSummaryResponse {

    private Long appointmentId;
    private String appointmentDate;
    private String startTime;
    private String endTime;
    private AppointmentStatus status;
    private BigDecimal totalCost;
    private VehicleBasicInfo vehicle;
    private Integer totalJobs;
    private Integer completedJobs;

    public AppointmentSummaryResponse() {}

    public AppointmentSummaryResponse(Long appointmentId, String appointmentDate, String startTime,
                                      String endTime, AppointmentStatus status, BigDecimal totalCost,
                                      VehicleBasicInfo vehicle, Integer totalJobs, Integer completedJobs) {
        this.appointmentId = appointmentId;
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.totalCost = totalCost;
        this.vehicle = vehicle;
        this.totalJobs = totalJobs;
        this.completedJobs = completedJobs;
    }

    // Getters and Setters
    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }

    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }

    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }

    public VehicleBasicInfo getVehicle() { return vehicle; }
    public void setVehicle(VehicleBasicInfo vehicle) { this.vehicle = vehicle; }

    public Integer getTotalJobs() { return totalJobs; }
    public void setTotalJobs(Integer totalJobs) { this.totalJobs = totalJobs; }

    public Integer getCompletedJobs() { return completedJobs; }
    public void setCompletedJobs(Integer completedJobs) { this.completedJobs = completedJobs; }

    /**
     * Nested class for basic vehicle information
     */
    public static class VehicleBasicInfo {
        private Long vehicleId;
        private String vehicleName;
        private String registrationNo;

        public VehicleBasicInfo() {}

        public VehicleBasicInfo(Long vehicleId, String vehicleName, String registrationNo) {
            this.vehicleId = vehicleId;
            this.vehicleName = vehicleName;
            this.registrationNo = registrationNo;
        }

        public Long getVehicleId() { return vehicleId; }
        public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

        public String getVehicleName() { return vehicleName; }
        public void setVehicleName(String vehicleName) { this.vehicleName = vehicleName; }

        public String getRegistrationNo() { return registrationNo; }
        public void setRegistrationNo(String registrationNo) { this.registrationNo = registrationNo; }
    }
}
