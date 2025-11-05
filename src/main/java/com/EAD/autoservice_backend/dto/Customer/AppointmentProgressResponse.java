package com.EAD.autoservice_backend.dto;

import com.EAD.autoservice_backend.model.Status;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for detailed appointment progress information
 */
public class AppointmentProgressResponse {
    
    private Long appointmentId;
    private String appointmentDate;
    private String startTime;
    private String endTime;
    private Status status;
    private BigDecimal totalCost;
    private VehicleInfo vehicle;
    private List<JobProgressInfo> jobs;

    public AppointmentProgressResponse() {}

    // Getters and Setters
    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }

    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }

    public VehicleInfo getVehicle() { return vehicle; }
    public void setVehicle(VehicleInfo vehicle) { this.vehicle = vehicle; }

    public List<JobProgressInfo> getJobs() { return jobs; }
    public void setJobs(List<JobProgressInfo> jobs) { this.jobs = jobs; }

    /**
     * Nested class for vehicle information
     */
    public static class VehicleInfo {
        private Long vehicleId;
        private String vehicleName;
        private String registrationNo;
        private String vehicleType;
        private String model;

        public VehicleInfo() {}

        public Long getVehicleId() { return vehicleId; }
        public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

        public String getVehicleName() { return vehicleName; }
        public void setVehicleName(String vehicleName) { this.vehicleName = vehicleName; }

        public String getRegistrationNo() { return registrationNo; }
        public void setRegistrationNo(String registrationNo) { this.registrationNo = registrationNo; }

        public String getVehicleType() { return vehicleType; }
        public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
    }

    /**
     * Nested class for job progress information
     */
    public static class JobProgressInfo {
        private Long appointmentJobId;
        private String serviceItemName;
        private String serviceItemType;
        private Status jobStatus;
        private String startTime;
        private String endTime;
        private BigDecimal additionalCost;
        private String jobNote;
        private Integer assignedEmployees;

        public JobProgressInfo() {}

        public Long getAppointmentJobId() { return appointmentJobId; }
        public void setAppointmentJobId(Long appointmentJobId) { this.appointmentJobId = appointmentJobId; }

        public String getServiceItemName() { return serviceItemName; }
        public void setServiceItemName(String serviceItemName) { this.serviceItemName = serviceItemName; }

        public String getServiceItemType() { return serviceItemType; }
        public void setServiceItemType(String serviceItemType) { this.serviceItemType = serviceItemType; }

        public Status getJobStatus() { return jobStatus; }
        public void setJobStatus(Status jobStatus) { this.jobStatus = jobStatus; }

        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }

        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }

        public BigDecimal getAdditionalCost() { return additionalCost; }
        public void setAdditionalCost(BigDecimal additionalCost) { this.additionalCost = additionalCost; }

        public String getJobNote() { return jobNote; }
        public void setJobNote(String jobNote) { this.jobNote = jobNote; }

        public Integer getAssignedEmployees() { return assignedEmployees; }
        public void setAssignedEmployees(Integer assignedEmployees) { this.assignedEmployees = assignedEmployees; }
    }
}