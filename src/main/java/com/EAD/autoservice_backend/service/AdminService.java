package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.ServiceItemRequest;
import com.EAD.autoservice_backend.dto.ServiceItemResponse;
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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for administrative operations.
 * Handles CRUD operations for service items and user management (Employees and Admins).
 */
@Service
@Transactional
public class AdminService {

    private final ServiceItemRepository serviceItemRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs an AdminService with the required dependencies.
     *
     * @param serviceItemRepository repository for service item operations
     * @param userRepository repository for user operations
     * @param passwordEncoder encoder for password hashing
     */
    public AdminService(ServiceItemRepository serviceItemRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.serviceItemRepository = serviceItemRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a new service item.
     *
     * @param request the service item creation request
     * @return the created service item
     * @throws BadRequestException if request contains invalid data
     */
    public ServiceItem createServiceItem(ServiceItemRequest request) {
        ServiceItem newItem = new ServiceItem();
        mapRequestToServiceItem(request, newItem);
        return mapServiceItemToResponse(serviceItemRepository.save(newItem));
    }

    /**
     * Retrieves a service item by its ID.
     *
     * @param id the service item ID
     * @return the service item
     * @throws ResourceNotFoundException if service item is not found
     */
    @Transactional(readOnly = true)
    public ServiceItemResponse getServiceById(Long id) {
        ServiceItem item = serviceItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceItem not found with id: " + id));
        return mapServiceItemToResponse(item);
    }

    /**
     * Retrieves all service items.
     *
     * @return list of all service items
     */
    @Transactional(readOnly = true)
    public List<ServiceItemResponse> getAllServices() {
        return serviceItemRepository.findAll()
                .stream()
                .map(this::mapServiceItemToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing service item.
     *
     * @param id the service item ID to update
     * @param request the update request containing new data
     * @return the updated service item
     * @throws ResourceNotFoundException if service item is not found
     * @throws BadRequestException if request contains invalid data
     */
    public ServiceItem updateService(Long id, ServiceItemRequest request) {
        ServiceItem existingService = getServiceById(id);
        mapRequestToServiceItem(request, existingService);
        return mapServiceItemToResponse(serviceItemRepository.save(existingService));
    }

    /**
     * Deletes a service item by its ID.
     *
     * @param id the service item ID to delete
     * @throws ResourceNotFoundException if service item is not found
     */
    public void deleteService(Long id) {
        if (!serviceItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("ServiceItem not found with id: " + id);
        }
        serviceItemRepository.deleteById(id);
    }

    /**
     * Creates a new user (Employee or Admin).
     *
     * @param request the user creation request
     * @return the created user response
     * @throws BadRequestException if request contains invalid data
     * @throws ResourceConflictException if username or email already exists
     */
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

        User savedUser = userRepository.save(newUser);
        return mapUserToResponse(savedUser);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the user ID
     * @return the user response
     * @throws ResourceNotFoundException if user is not found
     */
    @Transactional(readOnly = true)
    public UserCreateResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapUserToResponse(user);
    }

    /**
     * Retrieves all users.
     *
     * @return list of all user responses
     */
    @Transactional(readOnly = true)
    public List<UserCreateResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                // Only include employees and admins for this admin endpoint
                .filter(u -> (u instanceof Employee) || (u instanceof Admin))
                .map(this::mapUserToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing user.
     * Note: This implementation does not allow changing the role to prevent accidental privilege escalation.
     *
     * @param id the user ID to update
     * @param request the update request containing new data
     * @return the updated user response
     * @throws ResourceNotFoundException if user is not found
     * @throws ResourceConflictException if new username or email already exists
     */
    public UserCreateResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        validateUserUniqueness(request.username(), request.email(), id);

        Optional.ofNullable(request.username()).map(String::trim).ifPresent(user::setUsername);
        Optional.ofNullable(request.email()).map(String::trim).ifPresent(user::setEmail);

        User updatedUser = userRepository.save(user);
        return mapUserToResponse(updatedUser);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the user ID to delete
     * @throws ResourceNotFoundException if user is not found
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Maps a service item request to a service item entity.
     *
     * @param request the service item request
     * @param serviceItem the service item entity to populate
     * @throws BadRequestException if enum values are invalid
     */
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

    /**
     * Validates that username and email are unique.
     *
     * @param username the username to validate
     * @param email the email to validate
     * @param currentUserId the current user ID (for updates) or null (for creation)
     * @throws BadRequestException if username or email is blank
     * @throws ResourceConflictException if username or email already exists
     */
    private void validateUserUniqueness(String username, String email, Long currentUserId) {
        Optional.ofNullable(username).map(String::trim).ifPresent(u -> {
            if (u.isBlank())
                throw new BadRequestException("Username cannot be blank");
            userRepository.findByUsername(u).ifPresent(existingUser -> {
                if (!existingUser.getId().equals(currentUserId)) {
                    throw new ResourceConflictException("Error: Username is already taken!");
                }
            });
        });

        Optional.ofNullable(email).map(String::trim).ifPresent(e -> {
            if (e.isBlank())
                throw new BadRequestException("Email cannot be blank");
            userRepository.findByEmail(e).ifPresent(existingUser -> {
                if (!existingUser.getId().equals(currentUserId)) {
                    throw new ResourceConflictException("Error: User with this email is already in use!");
                }
            });
        });
    }

    /**
     * Maps a user entity to a user response DTO.
     *
     * @param user the user entity
     * @return the user response DTO
     */
    private UserCreateResponse mapUserToResponse(User user) {
        String role = "USER";
        if (user instanceof Admin) {
            role = "ADMIN";
        } else if (user instanceof Employee) {
            role = "EMPLOYEE";
        }

        // Safely handle potentially null fields
        String username = (user.getUsername() != null) ? user.getUsername() : "N/A";
        String email = (user.getEmail() != null) ? user.getEmail() : "N/A";

        return new UserCreateResponse(
                user.getId(),
                username,
                email,
                role,
                user.isRequiresPasswordChange());
    }
}
