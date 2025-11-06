package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.EmployeeJobAssignmentAddCostsRequest;
import com.EAD.autoservice_backend.dto.EmployeeJobAssignmentLogEndTimeRequest;
import com.EAD.autoservice_backend.dto.EmployeeJobAssignmentLogStartTimeRequest;
import com.EAD.autoservice_backend.dto.EmployeeJobAssignmentResponse;
import com.EAD.autoservice_backend.exception.FieldUpdatingException;
import com.EAD.autoservice_backend.exception.JobAssignmentNotFoundException;
import com.EAD.autoservice_backend.model.*;
import com.EAD.autoservice_backend.repository.AppointmentJobRepository;
import com.EAD.autoservice_backend.repository.AppointmentRepository;
import com.EAD.autoservice_backend.repository.JobAssignmentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobAssignmentService {

    private final JobAssignmentRepository jobAssignmentRepository;
    private final AppointmentJobRepository appointmentJobRepository;
    private final AppointmentRepository appointmentRepository;

    public JobAssignmentService(JobAssignmentRepository jobAssignmentRepository, AppointmentJobRepository appointmentJobRepository, AppointmentRepository appointmentRepository) {
        this.jobAssignmentRepository = jobAssignmentRepository;
        this.appointmentJobRepository = appointmentJobRepository;
        this.appointmentRepository = appointmentRepository;
    }

    // Get all job assignments for a specific appointment job id.
    public List<EmployeeJobAssignmentResponse> getJobAssignmentListByAppointmentJobId(Long appointmentJobId) {

        if (appointmentJobId == null) {
            throw new IllegalArgumentException("Appointment job ID cannot be empty");
        }

        List<JobAssignment> jobAssignmentList = jobAssignmentRepository.findByAppointmentJobId(appointmentJobId);

        return jobAssignmentList.stream()
                .map(this::toDto)
                .toList();

    }

    private EmployeeJobAssignmentResponse toDto(JobAssignment jobAssignment) {
        Employee employee = jobAssignment.getEmployee();
        Long employeeId = (employee != null) ? employee.getId() : null;
        String employeeUsername = (employee != null) ? employee.getUsername() : null;
        return new EmployeeJobAssignmentResponse(
                employeeId,
                employeeUsername,
                jobAssignment.getStartTime(),
                jobAssignment.getEndTime(),
                jobAssignment.getAdditionalCost(),
                jobAssignment.getCostNote()
        );
    }

    /*
    Log start time for a job assignment by an employee.
    Update the job status to ONGOING if it is still a NEW job.
    Update the appointment status to ONGOING if it is still at NEW status.
     */
    public EmployeeJobAssignmentResponse logStartTimeForJobAssignment(EmployeeJobAssignmentLogStartTimeRequest employeeJobAssignmentLogStartTimeRequest, Long jobAssignmentId) {
        try {
            JobAssignment jobAssignment = jobAssignmentRepository.findById(jobAssignmentId)
                    .orElseThrow(() -> new JobAssignmentNotFoundException("Job Assignment not found"));

            if (jobAssignment.getStartTime() != null) {
                throw new FieldUpdatingException("Job starting time has already been logged for this Job Assignment.");
            }else{
                // Save start time for the job assignment.
                jobAssignment.setStartTime(employeeJobAssignmentLogStartTimeRequest.getStartTime());
                JobAssignment updatedJobAssignment = jobAssignmentRepository.save(jobAssignment);

                // Update the appointment job status to Ongoing and update start time.
                AppointmentJob appointmentJob = jobAssignment.getAppointmentJob();
                Status jobStatus = parseStatus(employeeJobAssignmentLogStartTimeRequest.getStatus());
                appointmentJob.setJobStatus(jobStatus);
                appointmentJob.setStartTime(employeeJobAssignmentLogStartTimeRequest.getStartTime());
                appointmentJobRepository.save(appointmentJob);

                // Update the appointment status to Ongoing if it is not already.
                Appointment appointment = appointmentJob.getAppointment();
                if (appointment.getStatus().equals(Status.NEW)){
                    appointment.setStatus(jobStatus);
                    appointmentRepository.save(appointment);
                }

                return toDto(updatedJobAssignment);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to log time for Job Assignment: " + e.getMessage());
        }
    }


    // Helper method to safely parse status string to Status enum
    private Status parseStatus(String statusStr) {
        for (Status s : Status.values()) {
            if (s.name().equalsIgnoreCase(statusStr)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Invalid status value: " + statusStr);
    }


    // Log end time for a job assignment by an employee.
    public EmployeeJobAssignmentResponse logEndTimeForJobAssignment(EmployeeJobAssignmentLogEndTimeRequest employeeJobAssignmentLogEndTimeRequest, Long jobAssignmentId) {
        try {
            JobAssignment jobAssignment = jobAssignmentRepository.findById(jobAssignmentId)
                    .orElseThrow(() -> new JobAssignmentNotFoundException("Job Assignment not found"));

            if (jobAssignment.getEndTime() != null) {
                throw new FieldUpdatingException("Job ending time has already been logged for this Job Assignment.");
            }else{
                // Save end time for the job assignment.
                jobAssignment.setEndTime(employeeJobAssignmentLogEndTimeRequest.getEndTime());
                JobAssignment updatedJobAssignment = jobAssignmentRepository.save(jobAssignment);

                return toDto(updatedJobAssignment);
            }

        }catch (JobAssignmentNotFoundException | FieldUpdatingException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to log end time for Job Assignment: " + e.getMessage());
        }
    }

    // Add additional costs to an appointment job by an employee.
    public EmployeeJobAssignmentResponse addAdditionalCostsToAnAppointmentJob(EmployeeJobAssignmentAddCostsRequest employeeJobAssignmentAddCostsRequest, Long jobAssignmentId) {
                JobAssignment jobAssignment = jobAssignmentRepository.findById(jobAssignmentId)
                .orElseThrow(() -> new JobAssignmentNotFoundException("Job Assignment not found"));

        BigDecimal additionalCost = employeeJobAssignmentAddCostsRequest.getAdditionalcost();
        JobAssignment updatedJobAssignment;

        if (jobAssignment.getAdditionalCost() == null) {
            jobAssignment.setAdditionalCost(additionalCost);
            jobAssignment.setCostNote(employeeJobAssignmentAddCostsRequest.getCostNote());
            updatedJobAssignment = jobAssignmentRepository.save(jobAssignment);

        }else{
            throw new FieldUpdatingException("Additional cost has already been set for this Job Assignment.");
        }



        // Update the total additional cost in the AppointmentJob entity.
        AppointmentJob appointmentJob = jobAssignment.getAppointmentJob();
        BigDecimal totalJobCost = additionalCost.add(appointmentJob.getAdditionalCost() != null ? appointmentJob.getAdditionalCost() : BigDecimal.ZERO);
        appointmentJob.setAdditionalCost(totalJobCost);
        appointmentJobRepository.save(appointmentJob);

        //Update the total cost in appointment entity.
        Appointment appointment = appointmentJob.getAppointment();
        BigDecimal NewTotalAppointmentCost = additionalCost.add( appointment.getTotalCost() != null ? appointment.getTotalCost() : BigDecimal.ZERO);
        appointment.setTotalCost(NewTotalAppointmentCost);
        appointmentRepository.save(appointment);

        return toDto(updatedJobAssignment);
    }
}
