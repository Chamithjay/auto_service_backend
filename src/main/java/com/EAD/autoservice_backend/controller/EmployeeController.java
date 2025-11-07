package com.EAD.autoservice_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.EAD.autoservice_backend.model.*;
import com.EAD.autoservice_backend.service.*;
import java.util.Map;


// controller/EmployeeController.java
@RestController
@RequestMapping("/api/employee")
@CrossOrigin(origins = "http://localhost:3000")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/{employeeId}/dashboard")
    public ResponseEntity<Map<String, Object>> getEmployeeDashboard(
            @PathVariable Long employeeId) {
        Map<String, Object> dashboardData = employeeService.getEmployeeDashboard(employeeId);
        return ResponseEntity.ok(dashboardData);
    }

    @GetMapping("/{employeeId}/profile")
    public ResponseEntity<Employee> getEmployeeProfile(@PathVariable Long employeeId) {
        Employee employee = employeeService.getEmployeeProfile(employeeId);
        return ResponseEntity.ok(employee);
    }

    @PutMapping("/{employeeId}/profile")
    public ResponseEntity<Employee> updateEmployeeProfile(
            @PathVariable Long employeeId,
            @RequestBody Employee updatedEmployee) {
        Employee employee = employeeService.updateEmployeeProfile(employeeId, updatedEmployee);
        return ResponseEntity.ok(employee);
    }
}
