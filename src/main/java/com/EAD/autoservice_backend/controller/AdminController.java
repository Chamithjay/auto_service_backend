package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.ServiceItemRequest;
import com.EAD.autoservice_backend.dto.UserCreateRequest;
import com.EAD.autoservice_backend.dto.UserCreateResponse;
import com.EAD.autoservice_backend.model.ServiceItem;
import com.EAD.autoservice_backend.model.User;
import com.EAD.autoservice_backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // --- Service CRUD ---

    @PostMapping("/services")
    public ResponseEntity<ServiceItem> addServiceItem(@RequestBody ServiceItemRequest request) {
        ServiceItem createdItem = adminService.createServiceItem(request);
        return ResponseEntity.ok(createdItem);
    }

    @GetMapping("/services/{id}")
    public ResponseEntity<ServiceItem> getServiceItemById(@PathVariable Long id) {
        ServiceItem serviceItem = adminService.getServiceById(id);
        return ResponseEntity.ok(serviceItem);
    }

    @GetMapping("/services")
    public ResponseEntity<List<ServiceItem>> getAllServiceItems() {
        List<ServiceItem> items = adminService.getAllServices();
        return ResponseEntity.ok(items);
    }

    @PutMapping("/services/{id}")
    public ResponseEntity<ServiceItem> updateServiceItem(@PathVariable Long id, @RequestBody ServiceItemRequest request) {
        ServiceItem updatedItem = adminService.updateService(id, request);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/services/{id}")
    public ResponseEntity<Void> deleteServiceItem(@PathVariable Long id) {
        adminService.deleteService(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // --- User (Employee/Admin) CRUD ---

    @PostMapping("/users")
    public ResponseEntity<UserCreateResponse> createUser(@RequestBody UserCreateRequest request) {
        UserCreateResponse newUser = adminService.createUser(request);
        return ResponseEntity.ok(newUser);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserCreateResponse> getUserById(@PathVariable Long id) {
        UserCreateResponse user = adminService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserCreateResponse>> getAllUsers() {
        List<UserCreateResponse> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // a new DTO for updating a user without changing password

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}