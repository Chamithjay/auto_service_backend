package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.VehicleRequest;
import com.EAD.autoservice_backend.dto.CustomerVehicleResponse;
import com.EAD.autoservice_backend.exception.ResourceNotFoundException;
import com.EAD.autoservice_backend.exception.UserAlreadyExistsException;
import com.EAD.autoservice_backend.model.Customer;
import com.EAD.autoservice_backend.model.Vehicle;
import com.EAD.autoservice_backend.repository.CustomerRepository;
import com.EAD.autoservice_backend.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.EAD.autoservice_backend.dto.VehiclesDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for vehicle management operations.
 * Handles CRUD operations for customer vehicles including validation and duplicate checks.
 */
@Service
@Transactional
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Constructs a VehicleService with the required dependencies.
     *
     * @param vehicleRepository repository for vehicle operations
     * @param customerRepository repository for customer operations
     */
    @Autowired
    public VehicleService(VehicleRepository vehicleRepository, CustomerRepository customerRepository) {
        this.vehicleRepository = vehicleRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * Adds a new vehicle for a customer.
     * Validates that the registration number is unique for this customer.
     *
     * @param username the customer username
     * @param request the vehicle creation request
     * @return the created vehicle response
     * @throws ResourceNotFoundException if customer is not found
     * @throws UserAlreadyExistsException if registration number already exists for this customer
     */
    public CustomerVehicleResponse addVehicle(String username, VehicleRequest request) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with username: " + username));

        if (vehicleRepository.existsByRegistrationNoAndCustomerId(request.getRegistrationNo(), customer.getId())) {
            throw new UserAlreadyExistsException("Vehicle with registration number '" +
                    request.getRegistrationNo() + "' already exists for this customer");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleName(request.getVehicleName());
        vehicle.setRegistrationNo(request.getRegistrationNo());
        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setModel(request.getModel());
        vehicle.setCustomer(customer);
        vehicle.setCreatedAt(LocalDateTime.now());
        vehicle.setUpdatedAt(LocalDateTime.now());

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return mapToVehicleResponse(savedVehicle);
    }

    /**
     * Retrieves all vehicles for a customer.
     *
     * @param username the customer username
     * @return list of customer vehicle responses
     * @throws ResourceNotFoundException if customer is not found
     */
    public List<CustomerVehicleResponse> getCustomerVehicles(String username) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with username: " + username));

        List<Vehicle> vehicles = vehicleRepository.findByCustomerId(customer.getId());
        return vehicles.stream()
                .map(this::mapToVehicleResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a specific vehicle by its ID for a customer.
     *
     * @param username the customer username
     * @param vehicleId the vehicle ID
     * @return the vehicle response
     * @throws ResourceNotFoundException if customer or vehicle is not found
     */
    public CustomerVehicleResponse getVehicleById(String username, Long vehicleId) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with username: " + username));

        Vehicle vehicle = vehicleRepository.findByVehicleIdAndCustomerId(vehicleId, customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + vehicleId));

        return mapToVehicleResponse(vehicle);
    }

    /**
     * Updates vehicle information.
     * Validates that the new registration number (if changed) is unique for this customer.
     *
     * @param username the customer username
     * @param vehicleId the vehicle ID to update
     * @param request the vehicle update request
     * @return the updated vehicle response
     * @throws ResourceNotFoundException if customer or vehicle is not found
     * @throws UserAlreadyExistsException if new registration number already exists for this customer
     */
    public CustomerVehicleResponse updateVehicle(String username, Long vehicleId, VehicleRequest request) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with username: " + username));

        Vehicle vehicle = vehicleRepository.findByVehicleIdAndCustomerId(vehicleId, customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + vehicleId));

        if (!vehicle.getRegistrationNo().equals(request.getRegistrationNo())) {
            if (vehicleRepository.existsByRegistrationNoAndCustomerIdAndVehicleIdNot(
                    request.getRegistrationNo(), customer.getId(), vehicleId)) {
                throw new UserAlreadyExistsException("Vehicle with registration number '" +
                        request.getRegistrationNo() + "' already exists for this customer");
            }
        }

        vehicle.setVehicleName(request.getVehicleName());
        vehicle.setRegistrationNo(request.getRegistrationNo());
        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setModel(request.getModel());
        vehicle.setUpdatedAt(LocalDateTime.now());

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        return mapToVehicleResponse(updatedVehicle);
    }

    /**
     * Deletes a vehicle.
     *
     * @param username the customer username
     * @param vehicleId the vehicle ID to delete
     * @throws ResourceNotFoundException if customer or vehicle is not found
     */
    public void deleteVehicle(String username, Long vehicleId) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with username: " + username));

        Vehicle vehicle = vehicleRepository.findByVehicleIdAndCustomerId(vehicleId, customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + vehicleId));

        vehicleRepository.delete(vehicle);
    }

    /**
     * Maps a Vehicle entity to a CustomerVehicleResponse DTO.
     *
     * @param vehicle the vehicle entity
     * @return the vehicle response DTO
     */
    private CustomerVehicleResponse mapToVehicleResponse(Vehicle vehicle) {
        return new CustomerVehicleResponse(
                vehicle.getVehicleId(),
                vehicle.getVehicleName(),
                vehicle.getRegistrationNo(),
                vehicle.getVehicleType(),
                vehicle.getModel(),
                vehicle.getCreatedAt().format(DATE_TIME_FORMATTER),
                vehicle.getUpdatedAt().format(DATE_TIME_FORMATTER)
        );
    }

    /**
     * Retrieves all vehicles as DTOs.
     *
     * @return list of all vehicles as DTOs
     */
    public List<VehiclesDTO> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converts a Vehicle entity to a VehiclesDTO.
     *
     * @param vehicle the vehicle entity
     * @return the vehicles DTO
     */
    private VehiclesDTO convertToDTO(Vehicle vehicle) {
        return VehiclesDTO.builder()
                .vehicleId(vehicle.getVehicleId())
                .vehicleName(vehicle.getVehicleName())
                .registrationNo(vehicle.getRegistrationNo())
                .vehicleType(vehicle.getVehicleType())
                .model(vehicle.getModel())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .customerId(vehicle.getCustomer() != null ? vehicle.getCustomer().getId() : null)
                .build();
    }
}