package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.EmployeeVehicleResponse;
import com.EAD.autoservice_backend.service.VehicleService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    // Get vehicle information using vehicle id.
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeVehicleResponse> getVehicleById(@PathVariable("id") Long vehicleId){
        try{
            EmployeeVehicleResponse employeeVehicleResponse = vehicleService.getVehicleById(vehicleId);
            return new ResponseEntity<>(employeeVehicleResponse, HttpStatus.OK);
        }catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
