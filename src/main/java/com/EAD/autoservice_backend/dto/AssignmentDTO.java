package com.EAD.autoservice_backend.dto;

import com.EAD.autoservice_backend.model.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class AssignmentDTO {
    // Getters and Setters
    private Long assignmentId;
    private String customerName;
    private String vehicleInfo;
    private String serviceName;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Status jobStatus;
    private String jobNote;

    // Default constructor
    public AssignmentDTO() {}

    // Constructor from JobAssignment
    public AssignmentDTO(JobAssignment jobAssignment) {
        this.assignmentId = jobAssignment.getId();
        this.customerName = jobAssignment.getAppointmentJob().getAppointment().getVehicle().getCustomer().getUsername();
        this.vehicleInfo = jobAssignment.getAppointmentJob().getAppointment().getVehicle().getModel();
        this.serviceName = jobAssignment.getAppointmentJob().getServiceItem().getServiceItemName();
        this.appointmentDate = jobAssignment.getAppointmentJob().getAppointment().getAppointmentDate();
        this.startTime = jobAssignment.getAppointmentJob().getStartTime();
        this.endTime = jobAssignment.getAppointmentJob().getEndTime();
        this.jobStatus = Status.valueOf(jobAssignment.getAppointmentJob().getItemStatus().name());
        this.jobNote = jobAssignment.getAppointmentJob().getJobNote();
    }

    public void setAssignmentId(Long assignmentId) { this.assignmentId = assignmentId; }

    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public void setVehicleInfo(String vehicleInfo) { this.vehicleInfo = vehicleInfo; }

    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }

    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public void setJobStatus(Status jobStatus) { this.jobStatus = jobStatus; }

    public void setJobNote(String jobNote) { this.jobNote = jobNote; }
}