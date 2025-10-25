package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.VehicleResponse;
import com.EAD.autoservice_backend.model.Vehicle;
import com.EAD.autoservice_backend.repository.VehicleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    @Autowired
    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public VehicleResponse getVehicleById(Long vehicleId) {

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with ID: " + vehicleId));

        return new VehicleResponse(
                vehicle.getVehicleId(),
                vehicle.getRegistrationNo(),
                vehicle.getVehicleType().name(),
                vehicle.getModel()
        );

    }
}
