package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.ServiceItemRequest;
import com.EAD.autoservice_backend.dto.ServiceItemResponse;
import com.EAD.autoservice_backend.dto.UserCreateRequest;
import com.EAD.autoservice_backend.dto.UserCreateResponse;
import com.EAD.autoservice_backend.dto.UserUpdateRequest;
import com.EAD.autoservice_backend.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * REST controller for administrative operations.
 * Provides endpoints for managing service items and users (employees/admins).
 * All endpoints require ADMIN role.
 */
@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Creates a new service item.
     *
     * @param request the service item creation request
     * @return ResponseEntity containing the created service item
     */
    @PostMapping("/services")
    public ResponseEntity<ServiceItemResponse> addServiceItem(@Valid @RequestBody ServiceItemRequest request) {
        ServiceItemResponse createdItem = adminService.createServiceItem(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdItem.serviceItemId())
                .toUri();

        return ResponseEntity.created(location).body(createdItem);
    }

    /**
     * Retrieves a service item by its ID.
     *
     * @param id the service item ID
     * @return ResponseEntity containing the service item
     */
    @GetMapping("/services/{id}")
    public ResponseEntity<ServiceItemResponse> getServiceItemById(@PathVariable Long id) {
        ServiceItemResponse serviceItem = adminService.getServiceById(id);
        return ResponseEntity.ok(serviceItem);
    }

    /**
     * Retrieves all service items.
     *
     * @return ResponseEntity containing list of all service items
     */
    @GetMapping("/services")
    public ResponseEntity<List<ServiceItemResponse>> getAllServiceItems() {
        List<ServiceItemResponse> items = adminService.getAllServices();
        return ResponseEntity.ok(items);
    }

    /**
     * Updates an existing service item.
     *
     * @param id the service item ID
     * @param request the service item update request
     * @return ResponseEntity containing the updated service item
     */
    @PutMapping("/services/{id}")
    public ResponseEntity<ServiceItemResponse> updateServiceItem(@PathVariable Long id,
            @Valid @RequestBody ServiceItemRequest request) {
        ServiceItemResponse updatedItem = adminService.updateService(id, request);
        return ResponseEntity.ok(updatedItem);
    }

    /**
     * Deletes a service item.
     *
     * @param id the service item ID
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/services/{id}")
    public ResponseEntity<Void> deleteServiceItem(@PathVariable Long id) {
        adminService.deleteService(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Creates a new user (employee or admin).
     *
     * @param request the user creation request
     * @return ResponseEntity containing the created user details
     */
    @PostMapping("/employees")
    public ResponseEntity<UserCreateResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserCreateResponse newUser = adminService.createUser(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newUser.id())
                .toUri();

        return ResponseEntity.created(location).body(newUser);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the user ID
     * @return ResponseEntity containing the user details
     */
    @GetMapping({"/employees/{id}"})
    public ResponseEntity<UserCreateResponse> getUserById(@PathVariable Long id) {
        UserCreateResponse user = adminService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Retrieves all users (employees and admins).
     *
     * @return ResponseEntity containing list of all users
     */
    @GetMapping({"/employees"})
    public ResponseEntity<List<UserCreateResponse>> getAllUsers() {
        List<UserCreateResponse> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Updates an existing user.
     *
     * @param id the user ID
     * @param request the user update request
     * @return ResponseEntity containing the updated user details
     */
    @PutMapping({"/employees/{id}"})
    public ResponseEntity<UserCreateResponse> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        UserCreateResponse updated = adminService.updateUser(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes a user.
     *
     * @param id the user ID
     * @return ResponseEntity with no content
     */
    @DeleteMapping({"/employees/{id}"})
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}