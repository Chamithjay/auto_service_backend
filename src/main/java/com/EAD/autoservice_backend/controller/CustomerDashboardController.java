//package com.EAD.autoservice_backend.controller;
//
//import com.EAD.autoservice_backend.dto.Customer.AppointmentProgressResponse;
//import com.EAD.autoservice_backend.dto.Customer.AppointmentSummaryResponse;
//import com.EAD.autoservice_backend.dto.CustomerDashboardStatsResponse;
//import com.EAD.autoservice_backend.exception.ResourceNotFoundException;
//import com.EAD.autoservice_backend.service.CustomerDashboardService;
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
// * REST Controller for customer dashboard operations
// */
//@RestController
//@RequestMapping("/api/v1/customer/dashboard")
//public class CustomerDashboardController {
//
//    private final CustomerDashboardService dashboardService;
//
//    @Autowired
//    public CustomerDashboardController(CustomerDashboardService dashboardService) {
//        this.dashboardService = dashboardService;
//    }
//
//    /**
//     * Get dashboard statistics
//     * GET /api/v1/customer/dashboard/stats
//     */
//    @GetMapping("/stats")
//    public ResponseEntity<?> getDashboardStats(Authentication authentication) {
//        try {
//            String username = authentication.getName();
//            CustomerDashboardStatsResponse stats = dashboardService.getDashboardStats(username);
//            return ResponseEntity.ok(stats);
//        } catch (ResourceNotFoundException e) {
//            Map<String, String> error = new HashMap<>();
//            error.put("error", e.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
//        } catch (Exception e) {
//            Map<String, String> error = new HashMap<>();
//            error.put("error", "Failed to retrieve dashboard statistics");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//        }
//    }
//
//    /**
//     * Get all appointments for the customer
//     * GET /api/v1/customer/dashboard/appointments
//     */
//    @GetMapping("/appointments")
//    public ResponseEntity<?> getAllAppointments(Authentication authentication) {
//        try {
//            String username = authentication.getName();
//            List<AppointmentSummaryResponse> appointments = dashboardService.getCustomerAppointments(username);
//            return ResponseEntity.ok(appointments);
//        } catch (ResourceNotFoundException e) {
//            Map<String, String> error = new HashMap<>();
//            error.put("error", e.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
//        } catch (Exception e) {
//            Map<String, String> error = new HashMap<>();
//            error.put("error", "Failed to retrieve appointments");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//        }
//    }
//
//    /**
//     * Get active appointments only (NEW or ONGOING)
//     * GET /api/v1/customer/dashboard/appointments/active
//     */
//    @GetMapping("/appointments/active")
//    public ResponseEntity<?> getActiveAppointments(Authentication authentication) {
//        try {
//            String username = authentication.getName();
//            List<AppointmentSummaryResponse> appointments = dashboardService.getActiveAppointments(username);
//            return ResponseEntity.ok(appointments);
//        } catch (ResourceNotFoundException e) {
//            Map<String, String> error = new HashMap<>();
//            error.put("error", e.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
//        } catch (Exception e) {
//            Map<String, String> error = new HashMap<>();
//            error.put("error", "Failed to retrieve active appointments");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//        }
//    }
//
//    /**
//     * Get detailed progress for a specific appointment
//     * GET /api/v1/customer/dashboard/appointments/{id}/progress
//     */
//    @GetMapping("/appointments/{id}/progress")
//    public ResponseEntity<?> getAppointmentProgress(@PathVariable("id") Long appointmentId,
//                                                    Authentication authentication) {
//        try {
//            String username = authentication.getName();
//            AppointmentProgressResponse progress = dashboardService.getAppointmentProgress(username, appointmentId);
//            return ResponseEntity.ok(progress);
//        } catch (ResourceNotFoundException e) {
//            Map<String, String> error = new HashMap<>();
//            error.put("error", e.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
//        } catch (Exception e) {
//            Map<String, String> error = new HashMap<>();
//            error.put("error", "Failed to retrieve appointment progress");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//        }
//    }
//}