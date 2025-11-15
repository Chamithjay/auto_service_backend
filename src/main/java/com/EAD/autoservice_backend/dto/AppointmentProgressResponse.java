package com.EAD.autoservice_backend.dto;

import com.EAD.autoservice_backend.model.AppointmentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for detailed appointment progress information
 */
@Setter
@Getter
public class AppointmentProgressResponse {

    private Long appointmentId;
    private String appointmentDate;
    private String startTime;
    private String endTime;
    private AppointmentStatus status; // <-- changed
    private BigDecimal totalCost;
    private VehicleInfo vehicle;
    private List<JobProgressInfo> jobs;

    public AppointmentProgressResponse() {}

    @Setter
    @Getter
    public static class VehicleInfo {
        private Long vehicleId;
        private String vehicleName;
        private String registrationNo;
        private String vehicleType;
        private String model;

        public VehicleInfo() {}

    }

    @Setter
    @Getter
    public static class JobProgressInfo {
        private Long appointmentJobId;
        private String serviceItemName;
        private String serviceItemType;
        private AppointmentStatus jobStatus; // <-- changed
        private String startTime;
        private String endTime;
        private BigDecimal additionalCost;
        private String jobNote;
        private Integer assignedEmployees;

        public JobProgressInfo() {}

    }
}
