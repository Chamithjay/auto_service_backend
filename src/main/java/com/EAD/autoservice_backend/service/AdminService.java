package com.EAD.autoservice_backend.service;

// DTO imports
import com.EAD.autoservice_backend.dto.ServiceItemRequest;
import com.EAD.autoservice_backend.dto.UserCreateRequest;
import com.EAD.autoservice_backend.dto.UserCreateResponse;

// Model imports
import com.EAD.autoservice_backend.model.*; // Import all models

// Repository imports
import com.EAD.autoservice_backend.repository.ServiceItemRepository;
import com.EAD.autoservice_backend.repository.UserRepository;

// Exception imports
import com.EAD.autoservice_backend.exception.BadRequestException;
import com.EAD.autoservice_backend.exception.ResourceConflictException;

// Spring imports
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.EAD.autoservice_backend.exception.ResourceNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private ServiceItemRepository serviceItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    // --- Service CRUD Logic ---

    public ServiceItem createServiceItem(ServiceItemRequest request) {

        ServiceItem newItem = new ServiceItem();
        newItem.setServiceItemName(request.serviceItemName());
        newItem.setServiceItemCost(request.serviceItemCost());
        newItem.setRequiredEmployeeCount(request.requiredEmployeeCount());
        newItem.setEstimatedDuration(request.estimatedDuration());

        try {
            newItem.setVehicleType(VehicleType.valueOf(request.vehicleType().toUpperCase()));
            newItem.setServiceItemType(ServiceItemType.valueOf(request.serviceItemType().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid enum value provided for type: " + e.getMessage());
        }
        return serviceItemRepository.save(newItem);
    }

    public ServiceItem getServiceById(Long id) {
        return serviceItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceItem not found with id: " + id));
    }

    public List<ServiceItem> getAllServices() {
        return serviceItemRepository.findAll();
    }

    public ServiceItem updateService(Long id, ServiceItemRequest request) {
        // 1. Find the existing service or throw 404
        ServiceItem existingService = getServiceById(id);

        // 2. Update the fields from the DTO
        existingService.setServiceItemName(request.serviceItemName());
        existingService.setServiceItemCost(request.serviceItemCost());
        existingService.setRequiredEmployeeCount(request.requiredEmployeeCount());
        existingService.setEstimatedDuration(request.estimatedDuration());

        try {
            existingService.setVehicleType(VehicleType.valueOf(request.vehicleType().toUpperCase()));
            existingService.setServiceItemType(ServiceItemType.valueOf(request.serviceItemType().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid enum value provided for type: " + e.getMessage());
        }

        // 3. Save the updated object
        return serviceItemRepository.save(existingService);
    }

    public void deleteService(Long id) {
        if (!serviceItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("ServiceItem not found with id: " + id);
        }
        serviceItemRepository.deleteById(id);
    }


    // --- User (Employee/Admin) CRUD Logic ---

    public UserCreateResponse createUser(UserCreateRequest request) {

        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new ResourceConflictException("Error: Username is already taken!");
        }
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new ResourceConflictException("Error: Email is already in use!");
        }

        String hashedPassword = passwordEncoder.encode(request.password());
        User newUser;
        String role = request.role().toUpperCase();

        if (role.equals("EMPLOYEE")) {
            Employee newEmployee = new Employee();
            newEmployee.setUsername(request.username());
            newEmployee.setEmail(request.email());
            newEmployee.setPassword(hashedPassword);
            newUser = newEmployee;
        } else if (role.equals("ADMIN")) {
            Admin newAdmin = new Admin();
            newAdmin.setUsername(request.username());
            newAdmin.setEmail(request.email());
            newAdmin.setPassword(hashedPassword);
            newUser = newAdmin;
        } else {
            throw new BadRequestException("Invalid role specified. Use 'EMPLOYEE' or 'ADMIN'.");
        }

        User savedUser = userRepository.save(newUser);
        return mapUserToResponse(savedUser); // Use a helper method
    }

    public UserCreateResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapUserToResponse(user);
    }

    public List<UserCreateResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapUserToResponse) // Convert each User to a UserCreateResponse
                .collect(Collectors.toList());
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }


    private UserCreateResponse mapUserToResponse(User user) {
        return new UserCreateResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}