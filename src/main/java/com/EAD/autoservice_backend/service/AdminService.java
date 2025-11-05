package com.EAD.autoservice_backend.service;

// DTO imports
import com.EAD.autoservice_backend.dto.ServiceItemRequest;
import com.EAD.autoservice_backend.dto.UserCreateRequest;
import com.EAD.autoservice_backend.dto.UserCreateResponse;
import com.EAD.autoservice_backend.dto.UserUpdateRequest;

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
import java.util.Optional;

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

        // Username uniqueness
        String reqUsername = Optional.ofNullable(request.username()).map(String::trim).orElse("");
        if (reqUsername.isBlank()) {
            throw new BadRequestException("Username is required");
        }
        if (userRepository.findByUsername(reqUsername).isPresent()) {
            throw new ResourceConflictException("Error: Username is already taken!");
        }

        // Email uniqueness
        String reqEmail = Optional.ofNullable(request.email()).map(String::trim).orElse("");
        if (reqEmail.isBlank()) {
            throw new BadRequestException("Email is required");
        }
        if (userRepository.findByEmail(reqEmail).isPresent()) {
            throw new ResourceConflictException("Error: User with this email is already in use!");
        }

        if (request.password() == null || request.password().isBlank()) {
            throw new BadRequestException("Password is required");
        }

        String hashedPassword = passwordEncoder.encode(request.password());
        User newUser;
        String role = request.role().toUpperCase();

        if (role.equals("EMPLOYEE")) {
            Employee newEmployee = new Employee();
            newEmployee.setUsername(reqUsername);
            newEmployee.setEmail(reqEmail);
            newEmployee.setPassword(hashedPassword);
            newUser = newEmployee;
        } else if (role.equals("ADMIN")) {
            Admin newAdmin = new Admin();
            newAdmin.setUsername(reqUsername);
            newAdmin.setEmail(reqEmail);
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

    // Update user basic info (username, email, role validation)
    public UserCreateResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Normalize inputs
        String newUsername = Optional.ofNullable(request.username()).map(String::trim).orElse(null);
        String newEmail = Optional.ofNullable(request.email()).map(String::trim).orElse(null);
        String newRoleStr = Optional.ofNullable(request.role()).map(String::trim).orElse(null);

        // username conflict (case-insensitive), excluding self
        if (newUsername != null && !newUsername.equalsIgnoreCase(user.getUsername())) {
            userRepository.findByUsername(newUsername).ifPresent(conflict -> {
                if (!conflict.getId().equals(id)) {
                    throw new ResourceConflictException("Error: Username is already taken!");
                }
            });
            if (newUsername.isBlank()) {
                throw new BadRequestException("Username cannot be blank");
            }
            user.setUsername(newUsername);
        }

        // email conflict (case-insensitive), excluding self
        if (newEmail != null && !newEmail.equalsIgnoreCase(user.getEmail())) {
            userRepository.findByEmail(newEmail).ifPresent(conflict -> {
                if (!conflict.getId().equals(id)) {
                    throw new ResourceConflictException("Error: User with this email is already in use!");
                }
            });
            if (newEmail.isBlank()) {
                throw new BadRequestException("Email cannot be blank");
            }
            user.setEmail(newEmail);
        }

        // role validation: do not allow changing concrete type via this endpoint
        if (newRoleStr != null && !newRoleStr.isBlank()) {
            Role requestedRole;
            try {
                requestedRole = Role.valueOf(newRoleStr.toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new BadRequestException("Invalid role specified. Allowed: ADMIN, EMPLOYEE");
            }
            Role currentRole = user.getRole();
            if (!requestedRole.equals(currentRole)) {
                // Disallow changing discriminator type through simple update
                throw new BadRequestException("Changing user role type is not supported in updateUser. Create the desired type and migrate data instead.");
            }
        }

        // touch updatedAt
        user.setUpdatedAt(java.time.LocalDateTime.now());

        // Persist changes
        User saved = userRepository.save(user);
        return mapUserToResponse(saved);
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

