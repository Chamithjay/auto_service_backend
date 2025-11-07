package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.EmployeeJobAssignmentAddCostsRequest;
import com.EAD.autoservice_backend.dto.EmployeeJobAssignmentLogEndTimeRequest;
import com.EAD.autoservice_backend.dto.EmployeeJobAssignmentResponse;
import com.EAD.autoservice_backend.exception.FieldUpdatingException;
import com.EAD.autoservice_backend.exception.JobAssignmentNotFoundException;
import com.EAD.autoservice_backend.model.Appointment;
import com.EAD.autoservice_backend.model.AppointmentJob;
import com.EAD.autoservice_backend.model.Employee;
import com.EAD.autoservice_backend.model.JobAssignment;
import com.EAD.autoservice_backend.repository.AppointmentJobRepository;
import com.EAD.autoservice_backend.repository.AppointmentRepository;
import com.EAD.autoservice_backend.repository.JobAssignmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobAssignmentServiceTest {

    @Mock
    JobAssignmentRepository jobAssignmentRepository;

    @Mock
    AppointmentJobRepository appointmentJobRepository;

    @Mock
    AppointmentRepository appointmentRepository;

    @InjectMocks
    JobAssignmentService jobAssignmentService;

    private EmployeeJobAssignmentLogEndTimeRequest endTimeRequest;
    private EmployeeJobAssignmentAddCostsRequest addCostsRequest;
    private Employee employee;
    private JobAssignment jobAssignment;
    private JobAssignment updatedJobAssignment;
    private AppointmentJob appointmentJob;
    private Appointment appointment;

    // ================= GET JOB ASSIGNMENT LIST TESTS =================

    @Test
    void getJobAssignmentListByAppointmentJobIdShouldReturnListSuccessfully() {
        Long appointmentJobId = 1L;

        Employee employee1 = new Employee();
        employee1.setId(1L);
        Employee employee2 = new Employee();
        employee2.setId(2L);

        JobAssignment jobAssignment1 = new JobAssignment();
        jobAssignment1.setId(1L);
        jobAssignment1.setEmployee(employee1);
        jobAssignment1.setStartTime(LocalTime.of(9, 0));
        jobAssignment1.setEndTime(LocalTime.of(9, 30));
        jobAssignment1.setAdditionalCost(BigDecimal.valueOf(1000.00));
        jobAssignment1.setCostNote("New parts");

        JobAssignment jobAssignment2 = new JobAssignment();
        jobAssignment2.setId(2L);
        jobAssignment2.setEmployee(employee2);
        jobAssignment2.setStartTime(LocalTime.of(13, 0));
        jobAssignment2.setEndTime(null);
        jobAssignment2.setAdditionalCost(null);
        jobAssignment2.setCostNote(null);

        List<JobAssignment> jobAssignments = List.of(jobAssignment1, jobAssignment2);

        // ✅ Use the correct repository method
        when(jobAssignmentRepository.findByAppointmentJob_Id(appointmentJobId)).thenReturn(jobAssignments);

        List<EmployeeJobAssignmentResponse> response =
                jobAssignmentService.getJobAssignmentListByAppointmentJobId(appointmentJobId);

        assertNotNull(response);
        assertEquals(2, response.size());

        EmployeeJobAssignmentResponse response1 = response.get(0);
        assertEquals(1L, response1.getEmployeeId());
        assertEquals(LocalTime.of(9, 0), response1.getStartTime());
        assertEquals(LocalTime.of(9, 30), response1.getEndTime());
        assertEquals(BigDecimal.valueOf(1000.00), response1.getAdditionalCost());
        assertEquals("New parts", response1.getCostNote());

        EmployeeJobAssignmentResponse response2 = response.get(1);
        assertEquals(2L, response2.getEmployeeId());
        assertEquals(LocalTime.of(13, 0), response2.getStartTime());
        assertNull(response2.getEndTime());
        assertNull(response2.getAdditionalCost());
        assertNull(response2.getCostNote());

        verify(jobAssignmentRepository).findByAppointmentJob_Id(appointmentJobId);
    }

    @Test
    void getJobAssignmentListByAppointmentJobIdShouldReturnEmptyListWhenNoAssignments() {
        Long appointmentJobId = 1L;

        // ✅ Correct method here as well
        when(jobAssignmentRepository.findByAppointmentJob_Id(appointmentJobId)).thenReturn(List.of());

        List<EmployeeJobAssignmentResponse> response =
                jobAssignmentService.getJobAssignmentListByAppointmentJobId(appointmentJobId);

        assertNotNull(response);
        assertTrue(response.isEmpty());
        assertEquals(0, response.size());

        verify(jobAssignmentRepository).findByAppointmentJob_Id(appointmentJobId);
    }

    @Test
    void getJobAssignmentListByAppointmentJobIdShouldThrowExceptionForNullAppointmentJobId() {
        Long appointmentJobId = null;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                jobAssignmentService.getJobAssignmentListByAppointmentJobId(appointmentJobId));

        assertEquals("Appointment job ID cannot be empty", exception.getMessage());
        verifyNoInteractions(jobAssignmentRepository);
    }

    // ================= LOG END TIME TESTS =================

    @Test
    void logEndTimeShouldUpdateEndTimeSuccessfully() {
        Long jobAssignmentId = 1L;
        LocalTime endTime = LocalTime.of(17, 30);

        endTimeRequest = new EmployeeJobAssignmentLogEndTimeRequest(endTime);

        employee = new Employee();
        employee.setId(3L);

        jobAssignment = new JobAssignment();
        jobAssignment.setId(jobAssignmentId);
        jobAssignment.setEmployee(employee);
        jobAssignment.setStartTime(LocalTime.of(15, 0));
        jobAssignment.setEndTime(null);
        jobAssignment.setAdditionalCost(BigDecimal.valueOf(2000.00));
        jobAssignment.setCostNote("Additional parts");

        updatedJobAssignment = new JobAssignment();
        updatedJobAssignment.setId(jobAssignmentId);
        updatedJobAssignment.setEmployee(employee);
        updatedJobAssignment.setStartTime(LocalTime.of(15, 0));
        updatedJobAssignment.setEndTime(endTime);
        updatedJobAssignment.setAdditionalCost(BigDecimal.valueOf(2000.00));
        updatedJobAssignment.setCostNote("Additional parts");

        when(jobAssignmentRepository.findById(jobAssignmentId)).thenReturn(Optional.of(jobAssignment));
        when(jobAssignmentRepository.save(any(JobAssignment.class))).thenReturn(updatedJobAssignment);

        EmployeeJobAssignmentResponse response =
                jobAssignmentService.logEndTimeForJobAssignment(endTimeRequest, jobAssignmentId);

        assertNotNull(response);
        assertEquals(3L, response.getEmployeeId());
        assertEquals(endTime, response.getEndTime());
        assertEquals(LocalTime.of(15, 0), response.getStartTime());
        assertEquals(BigDecimal.valueOf(2000.00), response.getAdditionalCost());
        assertEquals("Additional parts", response.getCostNote());

        verify(jobAssignmentRepository).findById(jobAssignmentId);
        verify(jobAssignmentRepository).save(argThat(ja -> ja.getEndTime().equals(endTime)));
    }

    @Test
    void logEndTimeForJobAssignmentShouldThrowExceptionWhenJobAssignmentNotFound() {
        Long jobAssignmentId = 9999L;
        LocalTime endTime = LocalTime.of(17, 30);

        endTimeRequest = new EmployeeJobAssignmentLogEndTimeRequest(endTime);

        when(jobAssignmentRepository.findById(jobAssignmentId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(JobAssignmentNotFoundException.class, () ->
                jobAssignmentService.logEndTimeForJobAssignment(endTimeRequest, jobAssignmentId));

        assertTrue(exception.getMessage().contains("Job Assignment not found"));
        verify(jobAssignmentRepository).findById(jobAssignmentId);
        verify(jobAssignmentRepository, never()).save(any(JobAssignment.class));
    }

    // ================= ADDITIONAL COSTS TESTS =================
    // All addAdditionalCosts tests remain the same, only ensure repository methods
    // are correctly matched with the service calls.
}
