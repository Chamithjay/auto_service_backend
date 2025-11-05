package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.*;
import com.EAD.autoservice_backend.service.AppointmentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    // ✅ STEP 1: Get logged-in user info from JWT
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getLoggedUserInfo(
            @RequestHeader("Authorization") String authHeader
    ) {
        UserInfoResponse response = appointmentService.getLoggedUserInfo(authHeader);
        return ResponseEntity.ok(response);
    }


    // ✅ STEP 2: Get vehicles for logged-in user
    @GetMapping("/vehicles")
    public ResponseEntity<List<VehicleResponse>> getUserVehicles(
            //@RequestHeader("Authorization") String authHeader
            @RequestParam Long userId
    ) {
        List<VehicleResponse> response = appointmentService.getVehiclesForUser(userId);
        return ResponseEntity.ok(response);
    }


    // ✅ STEP 3: Get services & modifications for selected vehicle
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


    // ✅ STEP 4: Calculate total cost and end time (preview before submit)
    @PostMapping("/calculate")
    public ResponseEntity<AppointmentCalculationResponse> calculateAppointmentDetails(
            @RequestBody AppointmentCalculationRequest request
    ) {
        AppointmentCalculationResponse response =
                appointmentService.calculateAppointmentDetails(request);
        return ResponseEntity.ok(response);
    }

     //✅ STEP 5: Submit the final appointment
     @PostMapping("/create")
     public ResponseEntity<AppointmentResponse> createAppointment(
             @RequestBody AppointmentCreateRequest request,
             @RequestParam Long userId // 🔹 temporary for testing
     ) {
         AppointmentResponse response = appointmentService.createAppointment(request, userId); // pass userId directly
         return ResponseEntity.ok(response);
     }

//}

//    @GetMapping("/my-appointments")
//    public ResponseEntity<List<AppointmentHistoryResponse>> getCustomerAppointments(HttpServletRequest request) {
//        List<AppointmentHistoryResponse> response = appointmentService.getCustomerAppointments(request);
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/my-appointments")
    public ResponseEntity<List<AppointmentHistoryResponse>> getCustomerAppointments(
            @RequestParam Long userId) {
        List<AppointmentHistoryResponse> response = appointmentService.getCustomerAppointments(userId);
        return ResponseEntity.ok(response);
    }


}
