package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.AppointmentProgressResponse;
import com.EAD.autoservice_backend.dto.AppointmentSummaryResponse;
import com.EAD.autoservice_backend.dto.CustomerDashboardStatsResponse;
import com.EAD.autoservice_backend.exception.ResourceNotFoundException;
import com.EAD.autoservice_backend.service.CustomerDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for customer dashboard operations.
 * Provides endpoints for dashboard statistics, appointments overview, and progress tracking.
 */
@RestController
@RequestMapping("/api/v1/customer/dashboard")
public class CustomerDashboardController {

    private final CustomerDashboardService dashboardService;

    @Autowired
    public CustomerDashboardController(CustomerDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Retrieves dashboard statistics for the authenticated customer.
     *
     * @param authentication the authentication object containing user details
     * @return ResponseEntity containing dashboard statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats(Authentication authentication) {
        try {
            String username = authentication.getName();
            CustomerDashboardStatsResponse stats = dashboardService.getDashboardStats(username);
            return ResponseEntity.ok(stats);
        } catch (ResourceNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve dashboard statistics");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Retrieves all appointments for the authenticated customer.
     *
     * @param authentication the authentication object containing user details
     * @return ResponseEntity containing list of all appointments
     */
    @GetMapping("/appointments")
    public ResponseEntity<?> getAllAppointments(Authentication authentication) {
        try {
            String username = authentication.getName();
            List<AppointmentSummaryResponse> appointments = dashboardService.getCustomerAppointments(username);
            return ResponseEntity.ok(appointments);
        } catch (ResourceNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Retrieves active appointments (NEW or ONGOING status) for the authenticated customer.
     *
     * @param authentication the authentication object containing user details
     * @return ResponseEntity containing list of active appointments
     */
    @GetMapping("/appointments/active")
    public ResponseEntity<?> getActiveAppointments(Authentication authentication) {
        try {
            String username = authentication.getName();
            List<AppointmentSummaryResponse> appointments = dashboardService.getActiveAppointments(username);
            return ResponseEntity.ok(appointments);
        } catch (ResourceNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve active appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Retrieves detailed progress information for a specific appointment.
     *
     * @param appointmentId the appointment ID
     * @param authentication the authentication object containing user details
     * @return ResponseEntity containing appointment progress details
     */
    @GetMapping("/appointments/{id}/progress")
    public ResponseEntity<?> getAppointmentProgress(@PathVariable("id") Long appointmentId,
                                                    Authentication authentication) {
        try {
            String username = authentication.getName();
            AppointmentProgressResponse progress = dashboardService.getAppointmentProgress(username, appointmentId);
            return ResponseEntity.ok(progress);
        } catch (ResourceNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve appointment progress");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}