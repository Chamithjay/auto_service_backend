package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.*;
import com.EAD.autoservice_backend.service.AppointmentService;
import com.EAD.autoservice_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for appointment management.
 * Handles vehicle retrieval, service selection, appointment calculation and creation.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final JwtUtil jwtUtil;

    /**
     * Retrieves all vehicles for the authenticated user.
     *
     * @param authHeader the authorization header containing JWT token
     * @return ResponseEntity containing list of user's vehicles
     */
    @GetMapping("/vehicles")
    public ResponseEntity<List<VehicleResponse>> getUserVehicles(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        log.info("Extracting userId from token");
        Long userId = jwtUtil.extractUserId(token);
        log.info("Extracted userId: {}", userId);

        List<VehicleResponse> response = appointmentService.getVehiclesForUser(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves available services and modifications for a selected vehicle.
     *
     * @param request the vehicle selection request
     * @return ResponseEntity containing available services and modifications
     */
    @PostMapping("/services")
    public ResponseEntity<ServiceAndModificationResponse> getServicesAndModifications(
            @RequestBody VehicleSelectionRequest request
    ) {
        ServiceAndModificationResponse response =
                appointmentService.getServicesAndModificationsForVehicle(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Checks availability for appointment time slots.
     *
     * @param request the appointment calculation request
     * @return ResponseEntity containing availability information
     */
    @PostMapping("/availability")
    public ResponseEntity<AppointmentCalculationResponse> getAvailableTimes(
            @RequestBody AppointmentCalculationRequest request
    ) {
        AppointmentCalculationResponse response = appointmentService.calculateAppointmentDetails(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Calculates appointment details including total cost and duration.
     *
     * @param request the appointment calculation request
     * @return ResponseEntity containing calculation results
     */
    @PostMapping("/calculate")
    public ResponseEntity<AppointmentCalculationResponse> calculateAppointmentDetails(
            @RequestBody AppointmentCalculationRequest request
    ) {
        AppointmentCalculationResponse response =
                appointmentService.calculateAppointmentDetails(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Creates a new appointment for the authenticated user.
     *
     * @param request the appointment creation request
     * @param authHeader the authorization header containing JWT token
     * @return ResponseEntity containing the created appointment details
     */
    @PostMapping("/create")
    public ResponseEntity<AppointmentResponse> createAppointment(
            @RequestBody AppointmentCreateRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(token);

        AppointmentResponse response = appointmentService.createAppointment(request, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves appointment history for the authenticated customer.
     *
     * @param authHeader the authorization header containing JWT token
     * @param startDate optional start date for filtering
     * @param endDate optional end date for filtering
     * @return ResponseEntity containing list of appointment history
     */
    @GetMapping("/history")
    public ResponseEntity<List<AppointmentHistoryResponse>> getCustomerAppointmentHistory(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(token);

        List<AppointmentHistoryResponse> response =
                appointmentService.getCustomerAppointments(userId, startDate, endDate);

        return ResponseEntity.ok(response);
    }
}
