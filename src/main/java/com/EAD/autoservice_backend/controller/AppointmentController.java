package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.*;
import com.EAD.autoservice_backend.service.AppointmentService;
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
    public ResponseEntity<UserInfoResponse> getLoggedUserInfoTemp(
            @RequestParam Long userId // <-- temporarily pass userId
    ) {
        UserInfoResponse response = appointmentService.getLoggedUserInfoTemp(userId);
        return ResponseEntity.ok(response);
    }


    // ✅ STEP 2: Get vehicles for logged-in user
    @GetMapping("/vehicles")
    public ResponseEntity<List<VehicleResponse>> getUserVehiclesTemp(
            @RequestParam Long userId
    ) {
        List<VehicleResponse> response = appointmentService.getVehiclesForUserTemp(userId);
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


//    // ✅ STEP 4: Calculate total cost and end time (preview before submit)
//    @PostMapping("/calculate")
//    public ResponseEntity<AppointmentCalculationResponse> calculateAppointmentDetails(
//            @RequestBody AppointmentCalculationRequest request
//    ) {
//        AppointmentCalculationResponse response =
//                appointmentService.calculateAppointmentDetails(request);
//        return ResponseEntity.ok(response);
//    }
//
//     //✅ STEP 5: Submit the final appointment
//    @PostMapping("/create")
//    public ResponseEntity<AppointmentResponse> createAppointmentTemp(
//            @RequestBody AppointmentCreateRequest request,@RequestParam Long userId
//    ) {
//        AppointmentResponse response = appointmentService.createAppointmentTemp(request, userId);
//        return ResponseEntity.ok(response);
//    }
}
