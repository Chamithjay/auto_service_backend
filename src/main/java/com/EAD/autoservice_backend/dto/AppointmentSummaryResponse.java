package com.EAD.autoservice_backend.dto;

import com.EAD.autoservice_backend.model.AppointmentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO for appointment summary in customer dashboard
 */
@Setter
@Getter
public class AppointmentSummaryResponse {

    // Getters and Setters
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

    /**
     * Nested class for basic vehicle information
     */
    @Setter
    @Getter
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

    }
}
