package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.EmployeeProfileDTO;
import com.EAD.autoservice_backend.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for employee operations.
 * Provides endpoints for employee dashboard, profile viewing, and profile updates.
 */
@RestController
@RequestMapping("/api/v1/employee")
@CrossOrigin(origins = "http://localhost:3000")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * Retrieves the dashboard data for a specific employee.
     *
     * @param employeeId the employee ID
     * @return ResponseEntity containing the employee's dashboard data
     */
    @GetMapping("/{employeeId}/dashboard")
    public ResponseEntity<Map<String, Object>> getEmployeeDashboard(@PathVariable Long employeeId) {
        Map<String, Object> dashboardData = employeeService.getEmployeeDashboard(employeeId);
        return ResponseEntity.ok(dashboardData);
    }

    /**
     * Retrieves the profile information for a specific employee.
     *
     * @param employeeId the employee ID
     * @return ResponseEntity containing the employee's profile
     */
    @GetMapping("/{employeeId}/profile")
    public ResponseEntity<EmployeeProfileDTO> getEmployeeProfile(@PathVariable Long employeeId) {
        EmployeeProfileDTO profile = employeeService.getEmployeeProfile(employeeId);
        return ResponseEntity.ok(profile);
    }

    /**
     * Updates the profile information for a specific employee.
     *
     * @param employeeId the employee ID
     * @param updatedProfile the updated profile information
     * @return ResponseEntity containing the updated employee profile
     */
    @PutMapping("/{employeeId}/profile")
    public ResponseEntity<EmployeeProfileDTO> updateEmployeeProfile(
            @PathVariable Long employeeId,
            @RequestBody EmployeeProfileDTO updatedProfile) {

        EmployeeProfileDTO updated = employeeService.updateEmployeeProfile(employeeId, updatedProfile);
        return ResponseEntity.ok(updated);
    }
}
