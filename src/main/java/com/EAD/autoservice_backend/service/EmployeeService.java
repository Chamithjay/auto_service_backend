package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.model.*;
import com.EAD.autoservice_backend.repository.*;
import com.EAD.autoservice_backend.dto.AssignmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private JobAssignmentRepository jobAssignmentRepository;

    public Employee getEmployeeProfile(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public Employee updateEmployeeProfile(Long employeeId, Employee updatedEmployee) {
        Employee existingEmployee = getEmployeeProfile(employeeId);

        // Update only allowed fields
        existingEmployee.setPhoneNumber(updatedEmployee.getPhoneNumber());
        existingEmployee.setPosition(updatedEmployee.getPosition());
        existingEmployee.setDepartment(updatedEmployee.getDepartment());

        return employeeRepository.save(existingEmployee);
    }

    public Map<String, Object> getEmployeeDashboard(Long employeeId) {
        Map<String, Object> dashboardData = new HashMap<>();

        // Get today's job assignments and convert to DTO
        List<JobAssignment> todayAssignments = jobAssignmentRepository.findTodayAssignmentsByEmployee(employeeId);
        List<AssignmentDTO> todayDTOs = todayAssignments.stream()
                .map(AssignmentDTO::new)
                .collect(Collectors.toList());
        dashboardData.put("todayAssignments", todayDTOs);

        // Get upcoming job assignments and convert to DTO
        List<JobAssignment> upcomingAssignments = jobAssignmentRepository.findUpcomingAssignmentsByEmployee(employeeId);
        List<AssignmentDTO> upcomingDTOs = upcomingAssignments.stream()
                .map(AssignmentDTO::new)
                .collect(Collectors.toList());
        dashboardData.put("upcomingAssignments", upcomingDTOs);

        return dashboardData;
    }
}