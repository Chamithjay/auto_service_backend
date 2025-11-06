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

@RestController
@RequestMapping("/api/v1/appointment-jobs")
public class AppointmentJobController {

    private final AppointmentJobService appointmentJobService;

    public AppointmentJobController(AppointmentJobService appointmentJobService) {
        this.appointmentJobService = appointmentJobService;
    }


    // Get appointment job information by id.
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

    // Save job note for an appointment job.
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

    // Update job status for an appointment job.
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
