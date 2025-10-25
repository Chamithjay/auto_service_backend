package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.AppointmentJobResponse;
import com.EAD.autoservice_backend.service.AppointmentJobService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/appointment-jobs")
public class AppointmentJobController {

    private final AppointmentJobService appointmentJobService;

    public AppointmentJobController(AppointmentJobService appointmentJobService) {
        this.appointmentJobService = appointmentJobService;
    }


    // Get appointment job information by id.
    @RequestMapping("/{id}")
    public ResponseEntity<AppointmentJobResponse> getAppointmentJobById(@PathVariable("id") Long appointmentJobId){
        try{

            AppointmentJobResponse appointmentJobResponse = appointmentJobService.getAppointmentById(appointmentJobId);
            return ResponseEntity.ok(appointmentJobResponse);

        }catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }
}
