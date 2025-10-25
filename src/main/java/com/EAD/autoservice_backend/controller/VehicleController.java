package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.VehicleResponse;
import com.EAD.autoservice_backend.service.VehicleService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    @Autowired
    public VehicleController(VehicleService vehicleService) {

        this.vehicleService = vehicleService;
    }

    // Get vehicle information using vehicle id.
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable("id") Long vehicleId){
        try{
            VehicleResponse vehicleResponse = vehicleService.getVehicleById(vehicleId);

            return ResponseEntity.ok(vehicleResponse);

        }catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }
}
