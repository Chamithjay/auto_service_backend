package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.VehiclesDTO;
import com.EAD.autoservice_backend.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for vehicle management operations.
 * Handles retrieval of all vehicles in the system.
 */
@RestController
@RequestMapping("/api/v1/vehicles")
@CrossOrigin(origins = "*")
public class VehiclesController {

    @Autowired
    private VehicleService vehicleService;

    /**
     * Retrieves all vehicles in the system.
     *
     * @return list of all vehicles
     */
    @GetMapping
    public List<VehiclesDTO> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }
}