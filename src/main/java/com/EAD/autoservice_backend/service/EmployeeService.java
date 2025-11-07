package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.model.Employee;
import com.EAD.autoservice_backend.model.JobAssignment;
import com.EAD.autoservice_backend.dto.AssignmentDTO;
import com.EAD.autoservice_backend.repository.EmployeeRepository;
import com.EAD.autoservice_backend.repository.JobAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        LocalDate today = LocalDate.now();

        // Today's assignments
        List<JobAssignment> todayAssignments =
                jobAssignmentRepository.findByEmployee_IdAndAppointmentJob_Appointment_AppointmentDate(employeeId, today);
        List<AssignmentDTO> todayDTOs = todayAssignments.stream()
                .map(AssignmentDTO::new)
                .collect(Collectors.toList());
        dashboardData.put("todayAssignments", todayDTOs);

        // Upcoming assignments
        List<JobAssignment> upcomingAssignments =
                jobAssignmentRepository.findByEmployee_IdAndAppointmentJob_Appointment_AppointmentDateAfter(employeeId, today);
        List<AssignmentDTO> upcomingDTOs = upcomingAssignments.stream()
                .map(AssignmentDTO::new)
                .collect(Collectors.toList());
        dashboardData.put("upcomingAssignments", upcomingDTOs);

        return dashboardData;
    }
}
