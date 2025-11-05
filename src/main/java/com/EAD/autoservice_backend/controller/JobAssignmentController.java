package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.EmployeeJobAssignmentAddCostsRequest;
import com.EAD.autoservice_backend.dto.EmployeeJobAssignmentLogEndTimeRequest;
import com.EAD.autoservice_backend.dto.EmployeeJobAssignmentLogStartTimeRequest;
import com.EAD.autoservice_backend.dto.EmployeeJobAssignmentResponse;
import com.EAD.autoservice_backend.service.JobAssignmentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/job-assignments")
public class JobAssignmentController {

    private final JobAssignmentService jobAssignmentService;

    public JobAssignmentController(JobAssignmentService jobAssignmentService) {
        this.jobAssignmentService = jobAssignmentService;

    }

    // Find all employee records assigned to an appointment job.
    @GetMapping("/{id}")
    public ResponseEntity<List<EmployeeJobAssignmentResponse>> getJobAssignments(@PathVariable("id") Long appointmentJobId) {
        try {
            List<EmployeeJobAssignmentResponse> jobAssignmentList = jobAssignmentService.getJobAssignmentListByAppointmentJobId(appointmentJobId);
            return new ResponseEntity<>(jobAssignmentList, HttpStatus.OK);
        }catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Log employee start time for a job assignment.
    @PatchMapping("/log-start-time/{id}")
    public ResponseEntity<EmployeeJobAssignmentResponse> logTimeForJobAssignment(@PathVariable("id") Long jobAssignmentId, @Valid @RequestBody EmployeeJobAssignmentLogStartTimeRequest employeeJobAssignmentLogStartTimeRequest) {
        try{
        EmployeeJobAssignmentResponse updatedJobAssignment = jobAssignmentService.logStartTimeForJobAssignment(employeeJobAssignmentLogStartTimeRequest, jobAssignmentId);
        return new ResponseEntity<>(updatedJobAssignment, HttpStatus.OK);
        }catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Log employee end time for a job assignment.
    @PatchMapping("/log-end-time/{id}")
    public ResponseEntity<EmployeeJobAssignmentResponse> logEndTimeForJobAssignment(@PathVariable("id") Long jobAssignmentId, @Valid @RequestBody EmployeeJobAssignmentLogEndTimeRequest employeeJobAssignmentLogEndTimeRequest) {
        try{
            EmployeeJobAssignmentResponse updatedJobAssignment = jobAssignmentService.logEndTimeForJobAssignment(employeeJobAssignmentLogEndTimeRequest, jobAssignmentId);
            return new ResponseEntity<>(updatedJobAssignment, HttpStatus.OK);
        }catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Add additional costs to an appointment job.
    @PatchMapping("/add-costs/{id}")
    public ResponseEntity<EmployeeJobAssignmentResponse> addAdditionalCostsToAnAppointmentJob(@PathVariable("id") Long JobAssignmentId, @Valid @RequestBody EmployeeJobAssignmentAddCostsRequest employeeJobAssignmentAddCostsRequest) {
        try {
            EmployeeJobAssignmentResponse updatedJobAssignment = jobAssignmentService.addAdditionalCostsToAnAppointmentJob(employeeJobAssignmentAddCostsRequest, JobAssignmentId);
            return new ResponseEntity<>(updatedJobAssignment, HttpStatus.OK);
        }catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
