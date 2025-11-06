package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.EmployeeVehicleResponse;
import com.EAD.autoservice_backend.exception.VehicleNotFoundException;
import com.EAD.autoservice_backend.model.Vehicle;
import com.EAD.autoservice_backend.repository.VehicleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {

        this.vehicleRepository = vehicleRepository;
    }

    // Get vehicle details using vehicle ID.
    public EmployeeVehicleResponse getVehicleById(Long vehicleId) {

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with ID: " + vehicleId));

        return new EmployeeVehicleResponse(
                vehicle.getRegistrationNo(),
                vehicle.getVehicleType().name(),
                vehicle.getModel()
        );

    }
}
