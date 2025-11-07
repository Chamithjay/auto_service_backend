package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.LeaveReqDTO;
import com.EAD.autoservice_backend.model.*;
import com.EAD.autoservice_backend.repository.AdminRepository;
import com.EAD.autoservice_backend.repository.LeaveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveReqServiceTest {

    @Mock
    private LeaveRepository leaveRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private LeaveReqService leaveReqService;

    private Employee employee;
    private Leave leave;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setUsername("john_doe");
        employee.setEmail("john@example.com");

        leave = new Leave();
        leave.setLeaveId(101L);
        leave.setLeaveType(LeaveType.FULLDAY);
        leave.setLeaveDate(LocalDate.of(2025, 11, 5));
        leave.setLeaveReason("Fever");
        leave.setLeaveStatus(LeaveStatus.APPROVED);
        leave.setEmployee(employee);
    }

    @Test
    void testGetAllLeaves_ReturnsDTOList() {
        when(leaveRepository.findAll()).thenReturn(Arrays.asList(leave));
        List<LeaveReqDTO> result = leaveReqService.getAllLeaves();

        assertEquals(1, result.size());
        assertEquals("john_doe", result.get(0).getUsername());
    }

    @Test
    void testUpdateLeaveStatus_Approved() {
        when(leaveRepository.findById(101L)).thenReturn(Optional.of(leave));
        when(leaveRepository.save(any(Leave.class))).thenReturn(leave);

        leaveReqService.updateLeaveStatus(101L, LeaveStatus.APPROVED);

        verify(emailService, times(1)).sendEmail(
                eq("john@example.com"),
                contains("Approved"),
                contains("approved")
        );
        verify(leaveRepository, times(1)).save(leave);
    }
   @Test
void testUpdateLeaveStatus_Rejected() {
    when(leaveRepository.findById(101L)).thenReturn(Optional.of(leave));
    when(leaveRepository.save(any(Leave.class))).thenReturn(leave);

    leaveReqService.updateLeaveStatus(101L, LeaveStatus.REJECTED);

    verify(emailService, times(1)).sendEmail(
            eq("john@example.com"),
            contains("Rejected"),
            contains("rejected")
    );
    verify(leaveRepository, times(1)).save(leave);
}

    @Test
    void testUpdateLeaveStatus_LeaveNotFound() {
        when(leaveRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> leaveReqService.updateLeaveStatus(999L, LeaveStatus.APPROVED));

        assertEquals("Leave not found with ID: 999", exception.getMessage());
    }
}
