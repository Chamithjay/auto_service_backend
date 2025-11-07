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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.junit.jupiter.api.Assertions.*;
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


    // =============== GET JOB ASSIGNMENT LIST TESTS ===============

    @Test
    void getJobAssignmentListByAppointmentJobIdShouldReturnListSuccessfully() {
        // Given
        Long appointmentJobId = 1L;

        Employee employee1 = new Employee();
        employee1.setId(1L);

        Employee employee2 = new Employee();
        employee2.setId(2L);

        JobAssignment jobAssignment1 = new JobAssignment();
        jobAssignment1.setId(1L);

        jobAssignment1.setEmployee(employee1);
        jobAssignment1.setStartTime(LocalTime.of(9, 0, 0));
        jobAssignment1.setEndTime(LocalTime.of(9, 30, 0));
        jobAssignment1.setAdditionalCost(BigDecimal.valueOf(1000.00));
        jobAssignment1.setCostNote("New parts");

        JobAssignment jobAssignment2 = new JobAssignment();
        jobAssignment1.setId(2L);
        ;
        jobAssignment2.setEmployee(employee2);
        jobAssignment2.setStartTime(LocalTime.of(13, 0, 0));
        jobAssignment2.setEndTime(null);
        jobAssignment2.setAdditionalCost(null);
        jobAssignment2.setCostNote(null);

        List<JobAssignment> jobAssignments = List.of(jobAssignment1, jobAssignment2);

        when(jobAssignmentRepository.findByAppointmentJobId(appointmentJobId)).thenReturn(jobAssignments);

        // When
        List<EmployeeJobAssignmentResponse> response = jobAssignmentService.getJobAssignmentListByAppointmentJobId(appointmentJobId);

        // Then
        assertNotNull(response);
        assertEquals(2, response.size());

        // Verify first job assignment
        EmployeeJobAssignmentResponse response1 = response.get(0);
        assertEquals(1L, response1.getEmployeeId());
        assertEquals(LocalTime.of(9, 0, 0), response1.getStartTime());
        assertEquals(LocalTime.of(9, 30, 0), response1.getEndTime());
        assertEquals(BigDecimal.valueOf(1000.00), response1.getAdditionalCost());
        assertEquals("New parts", response1.getCostNote());

        // Verify second job assignment
        EmployeeJobAssignmentResponse response2 = response.get(1);
        assertEquals(2L, response2.getEmployeeId());
        assertEquals(LocalTime.of(13, 0, 0), response2.getStartTime());
        assertNull(response2.getEndTime());
        assertNull( response2.getAdditionalCost());
        assertNull( response2.getCostNote());

        verify(jobAssignmentRepository).findByAppointmentJobId(appointmentJobId);
    }

    @Test
    void getJobAssignmentListByAppointmentJobIdShouldReturnEmptyListWhenNoAssignments() {
        // Given
        Long appointmentJobId = 1L;
        List<JobAssignment> emptyList = List.of();

        when(jobAssignmentRepository.findByAppointmentJobId(appointmentJobId)).thenReturn(emptyList);

        // When
        List<EmployeeJobAssignmentResponse> response = jobAssignmentService.getJobAssignmentListByAppointmentJobId(appointmentJobId);

        // Then
        assertNotNull(response);
        assertTrue(response.isEmpty());
        assertEquals(0, response.size());

        verify(jobAssignmentRepository).findByAppointmentJobId(appointmentJobId);
    }

    @Test
    void getJobAssignmentListByAppointmentJobIdShouldThrowExceptionForNullAppointmentJobId() {
        // Given
        Long appointmentJobId = null;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            jobAssignmentService.getJobAssignmentListByAppointmentJobId(appointmentJobId));

        assertEquals("Appointment job ID cannot be empty", exception.getMessage());
        // Verify that the repository method was never called
        verifyNoInteractions(jobAssignmentRepository);
    }


    // =============== LOG END TIME TESTS ===============

    @Test
    void logEndTimeShouldUpdateEndTimeSuccessfully() {

        Long jobAssignmentId = 1L;
        LocalTime endTime = LocalTime.of(17, 30, 0);

         endTimeRequest = new EmployeeJobAssignmentLogEndTimeRequest(endTime);

        employee = new Employee();
        employee.setId(3L);

        jobAssignment = new JobAssignment();
        jobAssignment.setId(jobAssignmentId);
        jobAssignment.setEmployee(employee);
        jobAssignment.setStartTime(LocalTime.of(15, 0, 0));
        jobAssignment.setEndTime(null);
        jobAssignment.setAdditionalCost(BigDecimal.valueOf(2000.00));
        jobAssignment.setCostNote("Additional parts");

        updatedJobAssignment = new JobAssignment();
        updatedJobAssignment.setId(jobAssignmentId);
        updatedJobAssignment.setEmployee(employee);
        updatedJobAssignment.setStartTime(LocalTime.of(15, 0, 0));
        updatedJobAssignment.setEndTime(endTime);
        updatedJobAssignment.setAdditionalCost(BigDecimal.valueOf(2000.00));
        updatedJobAssignment.setCostNote("Additional parts");

        when(jobAssignmentRepository.findById(jobAssignmentId)).thenReturn(Optional.of(jobAssignment));
        when(jobAssignmentRepository.save(any(JobAssignment.class))).thenReturn(updatedJobAssignment);

        EmployeeJobAssignmentResponse response = jobAssignmentService.logEndTimeForJobAssignment(endTimeRequest, jobAssignmentId);

        assertNotNull(response);
        assertEquals(3L, response.getEmployeeId());
        assertEquals(endTime, response.getEndTime());
        assertEquals(LocalTime.of(15, 0, 0), response.getStartTime());
        assertEquals(BigDecimal.valueOf(2000.00), response.getAdditionalCost());
        assertEquals("Additional parts", response.getCostNote());

        verify(jobAssignmentRepository).findById(jobAssignmentId);
        verify(jobAssignmentRepository).save(argThat(ja -> ja.getEndTime().equals(endTime)));

    }

    @Test
    void logEndTimeForJobAssignmentShouldThrowExceptionWhenJobAssignmentNotFound() {

        Long jobAssignmentId = 9999L;
        LocalTime endTime = LocalTime.of(17, 30, 0);

         endTimeRequest = new EmployeeJobAssignmentLogEndTimeRequest(endTime);

        when(jobAssignmentRepository.findById(jobAssignmentId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(JobAssignmentNotFoundException.class, () -> {
            jobAssignmentService.logEndTimeForJobAssignment(endTimeRequest, jobAssignmentId);
        });

        String expectedMessage = "Job Assignment not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(jobAssignmentRepository).findById(jobAssignmentId);
        verify(jobAssignmentRepository, never()).save(any(JobAssignment.class));
    }


    @Test
    void logEndTimeForJobAssignmentShouldThrowExceptionWhenEndTimeAlreadyLogged() {

        Long jobAssignmentId = 1L;
        LocalTime newEndTime = LocalTime.of(18, 0, 0);

         endTimeRequest = new EmployeeJobAssignmentLogEndTimeRequest(newEndTime);

        jobAssignment = new JobAssignment();
        jobAssignment.setId(jobAssignmentId);
        jobAssignment.setEndTime(LocalTime.of(16, 0, 0));

        when(jobAssignmentRepository.findById(jobAssignmentId)).thenReturn(Optional.of(jobAssignment));

        FieldUpdatingException exception = assertThrows(FieldUpdatingException.class, () -> {
            jobAssignmentService.logEndTimeForJobAssignment(endTimeRequest, jobAssignmentId);
        });

        String expectedMessage = "Job ending time has already been logged for this Job Assignment.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(jobAssignmentRepository).findById(jobAssignmentId);
        verify(jobAssignmentRepository, never()).save(any(JobAssignment.class));
    }

    @Test
    void logEndTimeForJobAssignmentShouldThrowExceptionWhenRepositorySaveFails() {

        Long jobAssignmentId = 1L;
        LocalTime endTime = LocalTime.of(17, 30, 0);

         endTimeRequest = new EmployeeJobAssignmentLogEndTimeRequest(endTime);


        jobAssignment = new JobAssignment();
        jobAssignment.setId(jobAssignmentId);
        jobAssignment.setEndTime(null);

        when(jobAssignmentRepository.findById(jobAssignmentId)).thenReturn(Optional.of(jobAssignment));
        when(jobAssignmentRepository.save(any(JobAssignment.class))).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jobAssignmentService.logEndTimeForJobAssignment(endTimeRequest, jobAssignmentId);
        });

        String expectedMessage = "Failed to log end time for Job Assignment: Database error";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(jobAssignmentRepository).findById(jobAssignmentId);
        verify(jobAssignmentRepository).save(any(JobAssignment.class));
    }


    // =============== ADDITIONAL COSTS TESTS ===============

    @Test
    void addAdditionalCostsShouldSuccessfullyUpdateCostsAndPropagateToRelatedEntities() {
        // Given
        Long jobAssignmentId = 1L;
        BigDecimal additionalCost = BigDecimal.valueOf(500.00);
        String costNote = "Extra parts";

        addCostsRequest = new EmployeeJobAssignmentAddCostsRequest(additionalCost, costNote);

        // Setup employee
        employee = new Employee();
        employee.setId(3L);

        // Setup appointment
        appointment = new Appointment();
        appointment.setTotalCost(BigDecimal.valueOf(1000.00)); // Existing total cost

        // Setup appointment job
        appointmentJob = new AppointmentJob();
        appointmentJob.setAdditionalCost(BigDecimal.valueOf(200.00)); // Existing additional cost
        appointmentJob.setAppointment(appointment);

        // Setup job assignment
        JobAssignment jobAssignment = new JobAssignment();
        jobAssignment.setId(jobAssignmentId);
        jobAssignment.setEmployee(employee);
        jobAssignment.setAdditionalCost(null);
        jobAssignment.setCostNote(null);
        jobAssignment.setAppointmentJob(appointmentJob);

        // Setup updated job assignment
        updatedJobAssignment = new JobAssignment();
        updatedJobAssignment.setId(jobAssignmentId);
        updatedJobAssignment.setEmployee(employee);
        updatedJobAssignment.setAdditionalCost(additionalCost);
        updatedJobAssignment.setCostNote(costNote);
        updatedJobAssignment.setAppointmentJob(appointmentJob);

        // Mock repository calls
        when(jobAssignmentRepository.findById(jobAssignmentId)).thenReturn(Optional.of(jobAssignment));
        when(jobAssignmentRepository.save(any(JobAssignment.class))).thenReturn(updatedJobAssignment);

        // When
        EmployeeJobAssignmentResponse response = jobAssignmentService.addAdditionalCostsToAnAppointmentJob(addCostsRequest, jobAssignmentId);

        // Then
        assertNotNull(response);
        assertEquals(3L, response.getEmployeeId());
        assertEquals(additionalCost, response.getAdditionalCost());
        assertEquals(costNote, response.getCostNote());

        // Verify repository interactions
        verify(jobAssignmentRepository).findById(jobAssignmentId);
        verify(jobAssignmentRepository).save(argThat(ja ->
            ja.getAdditionalCost().equals(additionalCost) &&
            ja.getCostNote().equals(costNote)
        ));

        // Verify AppointmentJob cost update (existing 200 + new 500 = 700)
        verify(appointmentJobRepository).save(argThat(aj ->
            aj.getAdditionalCost().equals(BigDecimal.valueOf(700.00))
        ));

        // Verify Appointment total cost update (existing 1000 + new 500 = 1500)
        verify(appointmentRepository).save(argThat(a ->
            a.getTotalCost().equals(BigDecimal.valueOf(1500.00))
        ));
    }

    @Test
    void addAdditionalCostsShouldHandleNullExistingCostsInAppointmentJobAndAppointment() {
        // Given
        Long jobAssignmentId = 1L;
        BigDecimal additionalCost = BigDecimal.valueOf(300.00);
        String costNote = "New parts";

        addCostsRequest = new EmployeeJobAssignmentAddCostsRequest(additionalCost, costNote);

        Employee employee = new Employee();
        employee.setId(2L);

        // Setup appointment with null total cost
        Appointment appointment = new Appointment();
        appointment.setTotalCost(null);

        // Setup appointment job with null additional cost
        AppointmentJob appointmentJob = new AppointmentJob();
        appointmentJob.setAdditionalCost(null);
        appointmentJob.setAppointment(appointment);

        JobAssignment jobAssignment = new JobAssignment();
        jobAssignment.setId(jobAssignmentId);
        jobAssignment.setEmployee(employee);
        jobAssignment.setAdditionalCost(null);
        jobAssignment.setAppointmentJob(appointmentJob);

        JobAssignment updatedJobAssignment = new JobAssignment();
        updatedJobAssignment.setId(jobAssignmentId);
        updatedJobAssignment.setEmployee(employee);
        updatedJobAssignment.setAdditionalCost(additionalCost);
        updatedJobAssignment.setCostNote(costNote);
        updatedJobAssignment.setAppointmentJob(appointmentJob);

        when(jobAssignmentRepository.findById(jobAssignmentId)).thenReturn(Optional.of(jobAssignment));
        when(jobAssignmentRepository.save(any(JobAssignment.class))).thenReturn(updatedJobAssignment);

        // When
        EmployeeJobAssignmentResponse response = jobAssignmentService.addAdditionalCostsToAnAppointmentJob(addCostsRequest, jobAssignmentId);

        // Then
        assertNotNull(response);
        assertEquals(additionalCost, response.getAdditionalCost());
        assertEquals(costNote, response.getCostNote());

        // Verify null handling: 0 + 300 = 300
        verify(appointmentJobRepository).save(argThat(aj ->
            aj.getAdditionalCost().equals(BigDecimal.valueOf(300.00))
        ));

        verify(appointmentRepository).save(argThat(a ->
            a.getTotalCost().equals(BigDecimal.valueOf(300.00))
        ));
    }

    @Test
    void addAdditionalCostsShouldThrowExceptionWhenJobAssignmentNotFound() {
        // Given
        Long jobAssignmentId = 999L;
        BigDecimal additionalCost = BigDecimal.valueOf(500.00);
        String costNote = "Extra parts";

        addCostsRequest = new EmployeeJobAssignmentAddCostsRequest(additionalCost, costNote);

        when(jobAssignmentRepository.findById(jobAssignmentId)).thenReturn(Optional.empty());

        // When & Then
        JobAssignmentNotFoundException exception = assertThrows(JobAssignmentNotFoundException.class, () ->
            jobAssignmentService.addAdditionalCostsToAnAppointmentJob(addCostsRequest, jobAssignmentId));

        assertEquals("Job Assignment not found", exception.getMessage());
        verify(jobAssignmentRepository).findById(jobAssignmentId);
        verify(jobAssignmentRepository, never()).save(any(JobAssignment.class));
        verify(appointmentJobRepository, never()).save(any(AppointmentJob.class));
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void addAdditionalCostsShouldThrowExceptionWhenAdditionalCostAlreadySet() {
        // Given
        Long jobAssignmentId = 1L;
        BigDecimal newAdditionalCost = BigDecimal.valueOf(500.00);
        String costNote = "Extra parts";

        addCostsRequest = new EmployeeJobAssignmentAddCostsRequest(newAdditionalCost, costNote);

        JobAssignment jobAssignment = new JobAssignment();
        jobAssignment.setId(jobAssignmentId);
        jobAssignment.setAdditionalCost(BigDecimal.valueOf(200.00));

        when(jobAssignmentRepository.findById(jobAssignmentId)).thenReturn(Optional.of(jobAssignment));

        // When & Then
        FieldUpdatingException exception = assertThrows(FieldUpdatingException.class, () ->
            jobAssignmentService.addAdditionalCostsToAnAppointmentJob(addCostsRequest, jobAssignmentId));

        assertEquals("Additional cost has already been set for this Job Assignment.", exception.getMessage());
        verify(jobAssignmentRepository).findById(jobAssignmentId);
        verify(jobAssignmentRepository, never()).save(any(JobAssignment.class));
        verify(appointmentJobRepository, never()).save(any(AppointmentJob.class));
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void addAdditionalCostsShouldHandleZeroAdditionalCost() {
        // Given
        Long jobAssignmentId = 1L;
        BigDecimal zeroCost = BigDecimal.ZERO;
        String costNote = "No additional parts needed";

        addCostsRequest = new EmployeeJobAssignmentAddCostsRequest(zeroCost, costNote);

        Employee employee = new Employee();
        employee.setId(4L);

        Appointment appointment = new Appointment();
        appointment.setTotalCost(BigDecimal.valueOf(800.00));

        AppointmentJob appointmentJob = new AppointmentJob();
        appointmentJob.setAdditionalCost(BigDecimal.valueOf(100.00));
        appointmentJob.setAppointment(appointment);

        JobAssignment jobAssignment = new JobAssignment();
        jobAssignment.setId(jobAssignmentId);
        jobAssignment.setEmployee(employee);
        jobAssignment.setAdditionalCost(null);
        jobAssignment.setAppointmentJob(appointmentJob);

        JobAssignment updatedJobAssignment = new JobAssignment();
        updatedJobAssignment.setId(jobAssignmentId);
        updatedJobAssignment.setEmployee(employee);
        updatedJobAssignment.setAdditionalCost(zeroCost);
        updatedJobAssignment.setCostNote(costNote);
        updatedJobAssignment.setAppointmentJob(appointmentJob);

        when(jobAssignmentRepository.findById(jobAssignmentId)).thenReturn(Optional.of(jobAssignment));
        when(jobAssignmentRepository.save(any(JobAssignment.class))).thenReturn(updatedJobAssignment);

        // When
        EmployeeJobAssignmentResponse response = jobAssignmentService.addAdditionalCostsToAnAppointmentJob(addCostsRequest, jobAssignmentId);

        // Then
        assertNotNull(response);
        assertEquals(zeroCost, response.getAdditionalCost());
        assertEquals(costNote, response.getCostNote());

        // Verify costs remain unchanged when adding zero
        verify(appointmentJobRepository).save(argThat(aj ->
            aj.getAdditionalCost().equals(BigDecimal.valueOf(100.00))
        ));

        verify(appointmentRepository).save(argThat(a ->
            a.getTotalCost().equals(BigDecimal.valueOf(800.00))
        ));
    }



}


