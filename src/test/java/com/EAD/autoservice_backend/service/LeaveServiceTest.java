package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.EmployeeLeaveRequest;
import com.EAD.autoservice_backend.dto.EmployeeLeaveResponse;
import com.EAD.autoservice_backend.exception.EmployeeNotFoundException;
import com.EAD.autoservice_backend.model.Employee;
import com.EAD.autoservice_backend.model.Leave;
import com.EAD.autoservice_backend.model.LeaveStatus;
import com.EAD.autoservice_backend.model.LeaveType;
import com.EAD.autoservice_backend.repository.LeaveRepository;
import com.EAD.autoservice_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveServiceTest {

    @Mock
    private LeaveRepository leaveRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LeaveService leaveService;

    private Employee employee;
    private EmployeeLeaveRequest employeeLeaveRequest;
    private Leave savedLeave;

    @BeforeEach
    void setUp() {
        // Setup test employee
        employee = new Employee();
        employee.setId(3L);
    }

    @Test
    void requestLeaveShouldSaveLeaveRequestSuccessfully() {

        // Setup test leave request
        employeeLeaveRequest = new EmployeeLeaveRequest(
                3L,
                LocalDate.of(2025, 11, 15),
                "FULLDAY",
                "Family vacation"
        );

        // Setup saved leave response
        savedLeave = new Leave();
        savedLeave.setLeaveId(1L);
        savedLeave.setLeaveType(LeaveType.FULLDAY);
        savedLeave.setLeaveDate(LocalDate.of(2025, 11, 15));
        savedLeave.setLeaveReason("Family vacation");
        savedLeave.setEmployee(employee);
        savedLeave.setLeaveStatus(LeaveStatus.NEW);


        // Given
        when(userRepository.findById(3L)).thenReturn(Optional.of(employee));
        when(leaveRepository.save(any(Leave.class))).thenReturn(savedLeave);

        // When
        EmployeeLeaveResponse response = leaveService.requestLeave(employeeLeaveRequest);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getLeaveId());
        assertEquals("FULLDAY", response.getLeaveType());
        assertEquals(LocalDate.of(2025, 11, 15), response.getLeaveDate());
        assertEquals("Family vacation", response.getLeaveReason());
        assertEquals("NEW", response.getLeaveStatus());


        // Verify interactions
        verify(userRepository, times(1)).findById(3L);
        verify(leaveRepository, times(1)).save(any(Leave.class));

        // Verify that the leave object passed to save has correct properties
        verify(leaveRepository).save(argThat(leave ->
            leave.getLeaveDate().equals(LocalDate.of(2025, 11, 15)) &&
            leave.getLeaveType() == LeaveType.FULLDAY &&
            leave.getLeaveReason().equals("Family vacation") &&
            leave.getEmployee().equals(employee) &&
            leave.getLeaveStatus() == LeaveStatus.NEW
        ));
    }


    @Test
    void requestLeaveShouldThrowExceptionWhenEmployeeNotFound() {
        // Setup test leave request
        employeeLeaveRequest = new EmployeeLeaveRequest(
                9999L,
                LocalDate.of(2025, 11, 15),
                "FULLDAY",
                "Family vacation"
        );

        // Given
        when(userRepository.findById(9999L)).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(EmployeeNotFoundException.class, () -> {
            leaveService.requestLeave(employeeLeaveRequest);
        });

        String expectedMessage = "Employee not found with id: 9999";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        // Verify interactions
        verify(userRepository, times(1)).findById(9999L);
        verify(leaveRepository, never()).save(any(Leave.class));
    }


    @Test
    void requestLeaveShouldThrowExceptionWhenRepositorySaveFails() {

        employeeLeaveRequest = new EmployeeLeaveRequest(
                3L,
                LocalDate.of(2025, 11, 15),
                "FULLDAY",
                "Family vacation"
        );

        when(userRepository.findById(3L)).thenReturn(Optional.of(employee));
        when(leaveRepository.save(any(Leave.class))).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            leaveService.requestLeave(employeeLeaveRequest);
        });

        assertTrue(exception.getMessage().contains("Failed to request leave"));

        verify(userRepository, times(1)).findById(3L);
        verify(leaveRepository, times(1)).save(any(Leave.class));
    }


}