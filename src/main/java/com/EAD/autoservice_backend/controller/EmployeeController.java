package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.EmployeeProfileDTO;
import com.EAD.autoservice_backend.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/employee")
@CrossOrigin(origins = "http://localhost:3000")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/{employeeId}/dashboard")
    public ResponseEntity<Map<String, Object>> getEmployeeDashboard(@PathVariable Long employeeId) {
        Map<String, Object> dashboardData = employeeService.getEmployeeDashboard(employeeId);
        return ResponseEntity.ok(dashboardData);
    }

    @GetMapping("/{employeeId}/profile")
    public ResponseEntity<EmployeeProfileDTO> getEmployeeProfile(@PathVariable Long employeeId) {
        EmployeeProfileDTO profile = employeeService.getEmployeeProfile(employeeId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{employeeId}/profile")
    public ResponseEntity<EmployeeProfileDTO> updateEmployeeProfile(
            @PathVariable Long employeeId,
            @RequestBody EmployeeProfileDTO updatedProfile) {

        EmployeeProfileDTO updated = employeeService.updateEmployeeProfile(employeeId, updatedProfile);
        return ResponseEntity.ok(updated);
    }
}
