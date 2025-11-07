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

@Slf4j
@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final JwtUtil jwtUtil;

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

    @PostMapping("/services")
    public ResponseEntity<ServiceAndModificationResponse> getServicesAndModifications(
            @RequestBody VehicleSelectionRequest request
    ) {
        ServiceAndModificationResponse response =
                appointmentService.getServicesAndModificationsForVehicle(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/availability")
    public ResponseEntity<AppointmentCalculationResponse> getAvailableTimes(
            @RequestBody AppointmentCalculationRequest request
    ) {
        AppointmentCalculationResponse response = appointmentService.calculateAppointmentDetails(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/calculate")
    public ResponseEntity<AppointmentCalculationResponse> calculateAppointmentDetails(
            @RequestBody AppointmentCalculationRequest request
    ) {
        AppointmentCalculationResponse response =
                appointmentService.calculateAppointmentDetails(request);
        return ResponseEntity.ok(response);
    }

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
