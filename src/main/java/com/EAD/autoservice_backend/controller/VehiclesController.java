package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.VehiclesDTO;
import com.EAD.autoservice_backend.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*")
public class VehiclesController {

    @Autowired
    private VehicleService vehicleService;

    @GetMapping
    public List<VehiclesDTO> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }
}
