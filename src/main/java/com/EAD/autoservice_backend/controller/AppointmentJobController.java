package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.EmployeeAppointmentJobResponse;
import com.EAD.autoservice_backend.dto.EmployeeAppointmentJobStatusUpdateRequest;
import com.EAD.autoservice_backend.dto.EmployeeJobNoteRequest;
import com.EAD.autoservice_backend.service.AppointmentJobService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing appointment jobs.
 * Provides endpoints for retrieving job details, updating status, and saving notes.
 */
@RestController
@RequestMapping("/api/v1/appointment-jobs")
public class AppointmentJobController {

    private final AppointmentJobService appointmentJobService;

    public AppointmentJobController(AppointmentJobService appointmentJobService) {
        this.appointmentJobService = appointmentJobService;
    }

    /**
     * Retrieves appointment job information by ID.
     *
     * @param appointmentJobId the appointment job ID
     * @return ResponseEntity containing the appointment job details
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeAppointmentJobResponse> getAppointmentJobById(@PathVariable("id") Long appointmentJobId){
        try{
            EmployeeAppointmentJobResponse employeeAppointmentJobResponse = appointmentJobService.getAppointmentJobById(appointmentJobId);
            return new ResponseEntity<>(employeeAppointmentJobResponse, HttpStatus.OK);

        }catch (EntityNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Saves a job note for an appointment job.
     *
     * @param appointmentJobId the appointment job ID
     * @param employeeJobNoteRequest the job note request
     * @return ResponseEntity containing the updated appointment job details
     */
    @PatchMapping("/save-job-note/{id}")
    public ResponseEntity<EmployeeAppointmentJobResponse> saveJobNoteForAppointmentJob(@PathVariable("id") Long appointmentJobId, @Valid @RequestBody EmployeeJobNoteRequest employeeJobNoteRequest){
        try {
            EmployeeAppointmentJobResponse employeeAppointmentJobResponse = appointmentJobService.saveJobNoteForAppointmentJob(appointmentJobId, employeeJobNoteRequest);
            return new ResponseEntity<>(employeeAppointmentJobResponse, HttpStatus.OK);
        }catch (EntityNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates the job status for an appointment job.
     *
     * @param appointmentJobId the appointment job ID
     * @param statusUpdateRequest the status update request
     * @return ResponseEntity containing the status update message
     */
    @PatchMapping("/update-job-status/{id}")
    public ResponseEntity<String> updateJobStatusForAppointmentJob(@PathVariable("id") Long appointmentJobId, @Valid @RequestBody EmployeeAppointmentJobStatusUpdateRequest statusUpdateRequest){
        try {
            String response = appointmentJobService.updateAppointmentJobStatus(appointmentJobId, statusUpdateRequest.getJobStatus());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (EntityNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
