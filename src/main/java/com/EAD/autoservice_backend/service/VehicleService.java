//package com.EAD.autoservice_backend.service;
//
//import com.EAD.autoservice_backend.dto.VehicleRequest;
//import com.EAD.autoservice_backend.dto.VehicleResponse;
//import com.EAD.autoservice_backend.exception.ResourceNotFoundException;
//import com.EAD.autoservice_backend.exception.UnauthorizedAccessException;
//import com.EAD.autoservice_backend.exception.UserAlreadyExistsException;
//import com.EAD.autoservice_backend.model.Customer;
//import com.EAD.autoservice_backend.model.Vehicle;
//import com.EAD.autoservice_backend.repository.CustomerRepository;
//import com.EAD.autoservice_backend.repository.VehicleRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * Service for vehicle management operations
// */
//@Service
//@Transactional
//public class VehicleService {
//
//    private final VehicleRepository vehicleRepository;
//    private final CustomerRepository customerRepository;
//    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//    @Autowired
//    public VehicleService(VehicleRepository vehicleRepository, CustomerRepository customerRepository) {
//        this.vehicleRepository = vehicleRepository;
//        this.customerRepository = customerRepository;
//    }
//
//    /**
//     * Add a new vehicle for a customer
//     */
//    public VehicleResponse addVehicle(String username, VehicleRequest request) {
//        Customer customer = customerRepository.findByUsername(username)
//                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with username: " + username));
//
//        // Check if registration number already exists for this customer
//        if (vehicleRepository.existsByRegistrationNoAndCustomerId(request.getRegistrationNo(), customer.getId())) {
//            throw new UserAlreadyExistsException("Vehicle with registration number '" +
//                                                request.getRegistrationNo() + "' already exists for this customer");
//        }
//
//        Vehicle vehicle = new Vehicle();
//        vehicle.setVehicleName(request.getVehicleName());
//        vehicle.setRegistrationNo(request.getRegistrationNo());
//        vehicle.setVehicleType(request.getVehicleType());
//        vehicle.setModel(request.getModel());
//        vehicle.setCustomer(customer);
//        vehicle.setCreatedAt(LocalDateTime.now());
//        vehicle.setUpdatedAt(LocalDateTime.now());
//
//        Vehicle savedVehicle = vehicleRepository.save(vehicle);
//        return mapToVehicleResponse(savedVehicle);
//    }
//
//    /**
//     * Get all vehicles for a customer
//     */
//    public List<VehicleResponse> getCustomerVehicles(String username) {
//        Customer customer = customerRepository.findByUsername(username)
//                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with username: " + username));
//
//        List<Vehicle> vehicles = vehicleRepository.findByCustomerId(customer.getId());
//        return vehicles.stream()
//                .map(this::mapToVehicleResponse)
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Get a specific vehicle by ID
//     */
//    public VehicleResponse getVehicleById(String username, Long vehicleId) {
//        Customer customer = customerRepository.findByUsername(username)
//                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with username: " + username));
//
//        Vehicle vehicle = vehicleRepository.findByVehicleIdAndCustomerId(vehicleId, customer.getId())
//                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + vehicleId));
//
//        return mapToVehicleResponse(vehicle);
//    }
//
//    /**
//     * Update vehicle information
//     */
//    public VehicleResponse updateVehicle(String username, Long vehicleId, VehicleRequest request) {
//        Customer customer = customerRepository.findByUsername(username)
//                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with username: " + username));
//
//        Vehicle vehicle = vehicleRepository.findByVehicleIdAndCustomerId(vehicleId, customer.getId())
//                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + vehicleId));
//
//        // Check if registration number is being changed and if it already exists
//        if (!vehicle.getRegistrationNo().equals(request.getRegistrationNo())) {
//            if (vehicleRepository.existsByRegistrationNoAndCustomerIdAndVehicleIdNot(
//                    request.getRegistrationNo(), customer.getId(), vehicleId)) {
//                throw new UserAlreadyExistsException("Vehicle with registration number '" +
//                                                    request.getRegistrationNo() + "' already exists for this customer");
//            }
//        }
//
//        vehicle.setVehicleName(request.getVehicleName());
//        vehicle.setRegistrationNo(request.getRegistrationNo());
//        vehicle.setVehicleType(request.getVehicleType());
//        vehicle.setModel(request.getModel());
//        vehicle.setUpdatedAt(LocalDateTime.now());
//
//        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
//        return mapToVehicleResponse(updatedVehicle);
//    }
//
//    /**
//     * Delete a vehicle
//     */
//    public void deleteVehicle(String username, Long vehicleId) {
//        Customer customer = customerRepository.findByUsername(username)
//                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with username: " + username));
//
//        Vehicle vehicle = vehicleRepository.findByVehicleIdAndCustomerId(vehicleId, customer.getId())
//                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + vehicleId));
//
//        vehicleRepository.delete(vehicle);
//    }
//
//    /**
//     * Map Vehicle entity to VehicleResponse DTO
//     */
//    private VehicleResponse mapToVehicleResponse(Vehicle vehicle) {
//        return new VehicleResponse(
//                vehicle.getVehicleId(),
//                vehicle.getVehicleName(),
//                vehicle.getRegistrationNo(),
//                vehicle.getVehicleType(),
//                vehicle.getModel(),
//                vehicle.getCreatedAt().format(DATE_TIME_FORMATTER),
//                vehicle.getUpdatedAt().format(DATE_TIME_FORMATTER)
//        );
//    }
//}