package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.EmployeeVehicleResponse;
import com.EAD.autoservice_backend.dto.VehiclesDTO;
import com.EAD.autoservice_backend.exception.VehicleNotFoundException;
import com.EAD.autoservice_backend.model.Vehicle;
import com.EAD.autoservice_backend.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    @Autowired
    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    // Get vehicle details by ID
    public EmployeeVehicleResponse getVehicleById(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with ID: " + vehicleId));

        return new EmployeeVehicleResponse(
                vehicle.getRegistrationNo(),
                vehicle.getVehicleType().name(),
                vehicle.getModel()
        );
    }

    // Get all vehicles as DTOs
    public List<VehiclesDTO> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Convert Vehicle entity to VehiclesDTO
    private VehiclesDTO convertToDTO(Vehicle vehicle) {
        return VehiclesDTO.builder()
                .vehicleId(vehicle.getVehicleId())
                .vehicleName(vehicle.getVehicleName())
                .registrationNo(vehicle.getRegistrationNo())
                .vehicleType(vehicle.getVehicleType())
                .model(vehicle.getModel())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .customerId(vehicle.getCustomer() != null ? vehicle.getCustomer().getCustomerId() : null)
                .build();
    }
}
