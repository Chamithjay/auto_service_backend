package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.VehiclesDTO;
import com.EAD.autoservice_backend.model.Vehicle;
import com.EAD.autoservice_backend.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    public List<VehiclesDTO> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

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
