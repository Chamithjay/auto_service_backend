package com.EAD.autoservice_backend.service.integration;

import com.EAD.autoservice_backend.dto.EmployeeLeaveRequest;
import com.EAD.autoservice_backend.dto.EmployeeLeaveResponse;
import com.EAD.autoservice_backend.exception.EmployeeNotFoundException;
import com.EAD.autoservice_backend.model.Employee;
import com.EAD.autoservice_backend.model.Leave;
import com.EAD.autoservice_backend.model.LeaveStatus;
import com.EAD.autoservice_backend.repository.LeaveRepository;
import com.EAD.autoservice_backend.repository.UserRepository;
import com.EAD.autoservice_backend.service.LeaveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class LeaveServiceIntegrationTest {

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private UserRepository userRepository;

    private Employee testEmployee;
    private EmployeeLeaveRequest testLeaveRequest;

    @BeforeEach
    void setUp() {
        // Create abd save test employee
        testEmployee = new Employee();
        testEmployee.setUsername("John");
        testEmployee.setEmail("john@gmail.com");
        testEmployee.setPassword("john123");
        testEmployee.setPosition("Mechanic");
        testEmployee.setDepartment("Service");
        testEmployee.setEmployeeId("EMP001");

        testEmployee = userRepository.save(testEmployee);

        // Create test leave request
        testLeaveRequest = new EmployeeLeaveRequest(
                testEmployee.getId(),
                LocalDate.now().plusDays(3),
                "FULLDAY",
                "Medical Appointment"
        );

    }

    @Test
    @DisplayName("Should create a leave request successfully - integration test")
    void shouldCreateLeaveRequestSuccessfully() {

        // When - Call service to create leave request
        EmployeeLeaveResponse createdleave = leaveService.requestLeave(testLeaveRequest);

        // Then - Verify the leave request is saved correctly
        assertThat(createdleave).isNotNull();
        assertThat(createdleave.getLeaveDate()).isEqualTo(testLeaveRequest.getLeaveDate());
        assertThat(createdleave.getLeaveType()).isEqualTo(testLeaveRequest.getLeaveType());
        assertThat(createdleave.getLeaveReason()).isEqualTo(testLeaveRequest.getLeaveReason());
        assertThat(createdleave.getLeaveStatus()).isEqualTo("NEW");

        // Verify data exists in H2 database
        Optional<Leave> savedLeave = leaveRepository.findById(createdleave.getLeaveId());
        assertThat(savedLeave).isPresent();
        assertThat(savedLeave.get().getLeaveReason()).isEqualTo("Medical Appointment");
        assertThat(createdleave.getLeaveStatus()).isEqualTo("NEW");


    }

    @Test
    @DisplayName("Should handle employee not found exception - integration test")
    void shouldHandleEmployeeNotFoundException() {

        // Given
        EmployeeLeaveRequest employeeLeaveRequest = new EmployeeLeaveRequest(
                9999L,
                LocalDate.now().plusDays(3),
                "FULLDAY",
                "Medical Appointment"
        );

        // When & Then
        assertThatThrownBy(() -> leaveService.requestLeave(employeeLeaveRequest))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("Employee not found with id: 9999");
    }
}