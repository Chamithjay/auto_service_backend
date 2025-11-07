package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.AssignmentDTO;
import com.EAD.autoservice_backend.dto.EmployeeProfileDTO;
import com.EAD.autoservice_backend.model.Employee;
import com.EAD.autoservice_backend.model.JobAssignment;
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

    // Get employee profile as DTO
    public EmployeeProfileDTO getEmployeeProfile(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return new EmployeeProfileDTO(
                employee.getEmployeeId(),
                employee.getUsername(),
                employee.getEmail(),
                employee.getPosition(),
                employee.getDepartment(),
                employee.getPhoneNumber()
        );
    }

    // Update profile and return DTO
    public EmployeeProfileDTO updateEmployeeProfile(Long employeeId, EmployeeProfileDTO updatedDTO) {
        Employee existingEmployee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        existingEmployee.setPhoneNumber(updatedDTO.phoneNumber());
        existingEmployee.setPosition(updatedDTO.position());
        existingEmployee.setDepartment(updatedDTO.department());

        Employee saved = employeeRepository.save(existingEmployee);

        return new EmployeeProfileDTO(
                saved.getEmployeeId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getPosition(),
                saved.getDepartment(),
                saved.getPhoneNumber()
        );
    }

    // Dashboard
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
