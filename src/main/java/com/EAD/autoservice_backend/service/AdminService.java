package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.ServiceItemRequest;
import com.EAD.autoservice_backend.dto.UserCreateRequest;
import com.EAD.autoservice_backend.dto.UserCreateResponse;
import com.EAD.autoservice_backend.dto.UserUpdateRequest;
import com.EAD.autoservice_backend.exception.BadRequestException;
import com.EAD.autoservice_backend.exception.ResourceConflictException;
import com.EAD.autoservice_backend.exception.ResourceNotFoundException;
import com.EAD.autoservice_backend.model.Admin;
import com.EAD.autoservice_backend.model.Employee;
import com.EAD.autoservice_backend.model.ServiceItem;
import com.EAD.autoservice_backend.model.ServiceItemType;
import com.EAD.autoservice_backend.model.User;
import com.EAD.autoservice_backend.model.VehicleType;
import com.EAD.autoservice_backend.repository.ServiceItemRepository;
import com.EAD.autoservice_backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminService {

    private final ServiceItemRepository serviceItemRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(ServiceItemRepository serviceItemRepository,
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder) {
        this.serviceItemRepository = serviceItemRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // --- Service CRUD Logic ---

    public ServiceItem createServiceItem(ServiceItemRequest request) {
        ServiceItem newItem = new ServiceItem();
        mapRequestToServiceItem(request, newItem);
        return serviceItemRepository.save(newItem);
    }

    @Transactional(readOnly = true)
    public ServiceItem getServiceById(Long id) {
        return serviceItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceItem not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<ServiceItem> getAllServices() {
        return serviceItemRepository.findAll();
    }

    public ServiceItem updateService(Long id, ServiceItemRequest request) {
        ServiceItem existingService = getServiceById(id);
        mapRequestToServiceItem(request, existingService);
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
        validateUserUniqueness(request.username(), request.email(), null);

        if (request.password() == null || request.password().isBlank()) {
            throw new BadRequestException("Password is required");
        }

        User newUser;
        String role = Optional.ofNullable(request.role()).map(String::toUpperCase).orElse("");

        newUser = switch (role) {
            case "EMPLOYEE" -> new Employee();
            case "ADMIN" -> new Admin();
            default -> throw new BadRequestException("Invalid role specified. Use 'EMPLOYEE' or 'ADMIN'.");
        };

        newUser.setUsername(request.username().trim());
        newUser.setEmail(request.email().trim());
        newUser.setPassword(passwordEncoder.encode(request.password()));
        // requiresPasswordChange is true by default from the entity definition

        User savedUser = userRepository.save(newUser);
        return mapUserToResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public UserCreateResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapUserToResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserCreateResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapUserToResponse)
                .collect(Collectors.toList());
    }

    public UserCreateResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        validateUserUniqueness(request.username(), request.email(), id);

        Optional.ofNullable(request.username()).map(String::trim).ifPresent(user::setUsername);
        Optional.ofNullable(request.email()).map(String::trim).ifPresent(user::setEmail);

        // Note: This implementation does not allow changing the role of an existing user.
        // This is often a good security practice to prevent accidental privilege escalation.

        User updatedUser = userRepository.save(user);
        return mapUserToResponse(updatedUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    // --- Helper Methods ---

    private void mapRequestToServiceItem(ServiceItemRequest request, ServiceItem serviceItem) {
        serviceItem.setServiceItemName(request.serviceItemName());
        serviceItem.setServiceItemCost(request.serviceItemCost());
        serviceItem.setRequiredEmployeeCount(request.requiredEmployeeCount());
        serviceItem.setEstimatedDuration(request.estimatedDuration());

        try {
            serviceItem.setVehicleType(VehicleType.valueOf(request.vehicleType().toUpperCase()));
            serviceItem.setServiceItemType(ServiceItemType.valueOf(request.serviceItemType().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid enum value provided for type: " + e.getMessage());
        }
    }

    private void validateUserUniqueness(String username, String email, Long currentUserId) {
        Optional.ofNullable(username).map(String::trim).ifPresent(u -> {
            if (u.isBlank()) throw new BadRequestException("Username cannot be blank");
            userRepository.findByUsername(u).ifPresent(existingUser -> {
                if (!existingUser.getId().equals(currentUserId)) {
                    throw new ResourceConflictException("Error: Username is already taken!");
                }
            });
        });

        Optional.ofNullable(email).map(String::trim).ifPresent(e -> {
            if (e.isBlank()) throw new BadRequestException("Email cannot be blank");
            userRepository.findByEmail(e).ifPresent(existingUser -> {
                if (!existingUser.getId().equals(currentUserId)) {
                    throw new ResourceConflictException("Error: User with this email is already in use!");
                }
            });
        });
    }

    private UserCreateResponse mapUserToResponse(User user) {
        String role = "USER"; // Default
        if (user instanceof Admin) {
            role = "ADMIN";
        } else if (user instanceof Employee) {
            role = "EMPLOYEE";
        }
        return new UserCreateResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                role,
                user.isRequiresPasswordChange()
        );
    }
}
