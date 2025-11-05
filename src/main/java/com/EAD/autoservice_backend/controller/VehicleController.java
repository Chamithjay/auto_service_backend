//package com.EAD.autoservice_backend.controller;
//
//import com.EAD.autoservice_backend.dto.Customer.VehicleRequest;
//import com.EAD.autoservice_backend.dto.Customer.VehicleResponse;
//import com.EAD.autoservice_backend.exception.ResourceNotFoundException;
//import com.EAD.autoservice_backend.exception.UserAlreadyExistsException;
//import com.EAD.autoservice_backend.service.VehicleService;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * REST Controller for vehicle management operations
// */
//@RestController
//@RequestMapping("/api/v1/customer/vehicles")
//public class VehicleController {
//
//    private final VehicleService vehicleService;
//
//    @Autowired
//    public VehicleController(VehicleService vehicleService) {
//        this.vehicleService = vehicleService;
//    }
//
//    /**
//     * Add a new vehicle
//     * POST /api/v1/customer/vehicles
//     */
//    @PostMapping
//    public ResponseEntity<?> addVehicle(@Valid @RequestBody VehicleRequest request,
//                                       Authentication authentication) {
//        try {
//            String username = authentication.getName();
//            VehicleResponse vehicle = vehicleService.addVehicle(username, request);
//            return ResponseEntity.status(HttpStatus.CREATED).body(vehicle);
//        } catch (ResourceNotFoundException e) {
//            Map<String, String> error = new HashMap<>();
//            error.put("error", e.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
//        } catch (UserAlreadyExistsException e) {
//            Map<String, String> error = new HashMap<>();
//            error.put("error", e.getMessage());
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
//        } catch (Exception e) {
//            Map<String, String> error = new HashMap<>();
//            error.put("error", "Failed to add vehicle");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//        }
//    }
//
//    /**
//     * Get all vehicles for the current customer
//     * GET /api/v1/customer/vehicles
//     */
//    @GetMapping
//    public ResponseEntity<?> getAllVehicles(Authentication authentication) {
//        try {
//            String username = authentication.getName();
//            List<VehicleResponse> vehicles = vehicleService.getCustomerVehicles(username);
//            return ResponseEntity.ok(vehicles);
//        } catch (ResourceNotFoundException e) {
//            Map<String, String> error = new HashMap<>();
//            error.put("error", e.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
//        } catch (Exception e) {
//            Map<String, String> error = new HashMap<>();
//            error.put("error", "Failed to retrieve vehicles");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//        }
//    }
//
//    /**
//     * Get a specific vehicle by ID
//     * GET /api/v1/customer/vehicles/{id}
//     */
//    @GetMapping("/{id}")
//    public ResponseEntity<?> getVehicleById(@PathVariable("id") Long vehicleId,
//                                           Authentication authentication) {
//        try {
//            String username = authentication.getName();
//            VehicleResponse vehicle = vehicleService.getVehicleById(username, vehicleId);
//            return ResponseEntity.ok(vehicle);
//        } catch (ResourceNotFoundException e) {
//            Map<String, String> error = new HashMap<>();
//            error.put("error", e.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
//        } catch (Exception e) {
//            Map<String, String> error = new HashMap<>();
//            error.put("error", "Failed to retrieve vehicle");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//        }
//    }
//
//    /**
//     * Update vehicle information
//     * PUT /api/v1/customer/vehicles/{id}
//     */
//    @PutMapping("/{id}")
//    public ResponseEntity<?> updateVehicle(@PathVariable("id") Long vehicleId,
//                                          @Valid @RequestBody VehicleRequest request,
//                                          Authentication authentication) {
//        try {
//            String username = authentication.getName();
//            VehicleResponse updatedVehicle = vehicleService.updateVehicle(username, vehicleId, request);
//            return ResponseEntity.ok(updatedVehicle);
//        } catch (ResourceNotFoundException e) {
//            Map<String, String> error = new HashMap<>();
//            error.put("error", e.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
//        } catch (UserAlreadyExistsException e) {
//            Map<String, String> error = new HashMap<>();
//            error.put("error", e.getMessage());
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
//        } catch (Exception e) {
//            Map<String, String> error = new HashMap<>();
//            error.put("error", "Failed to update vehicle");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//        }
//    }
//
//    /**
//     * Delete a vehicle
//     * DELETE /api/v1/customer/vehicles/{id}
//     */
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deleteVehicle(@PathVariable("id") Long vehicleId,
//                                          Authentication authentication) {
//        try {
//            String username = authentication.getName();
//            vehicleService.deleteVehicle(username, vehicleId);
//            Map<String, String> response = new HashMap<>();
//            response.put("message", "Vehicle deleted successfully");
//            return ResponseEntity.ok(response);
//        } catch (ResourceNotFoundException e) {
//            Map<String, String> error = new HashMap<>();
//            error.put("error", e.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
//        } catch (Exception e) {
//            Map<String, String> error = new HashMap<>();
//            error.put("error", "Failed to delete vehicle");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//        }
//    }
//}