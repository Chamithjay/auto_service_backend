package com.EAD.autoservice_backend.service.unit;

import com.EAD.autoservice_backend.dto.*;
import com.EAD.autoservice_backend.exception.AppointmentJobNotFoundException;
import com.EAD.autoservice_backend.exception.DetailsMissingException;
import com.EAD.autoservice_backend.model.*;
import com.EAD.autoservice_backend.repository.AppointmentJobRepository;
import com.EAD.autoservice_backend.service.AppointmentJobService;
import com.EAD.autoservice_backend.service.JobAssignmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentJobServiceTest {

    @Mock
    AppointmentJobRepository appointmentJobRepository;
    @Mock
    JobAssignmentService jobAssignmentService;

    @InjectMocks
    AppointmentJobService appointmentJobService;

    private AppointmentJob appointmentJob;
    private Appointment appointment;
    private Vehicle vehicle;
    private Customer customer;
    private ServiceItem serviceItem;
    private List<EmployeeJobAssignmentResponse> jobAssignmentList;

    @BeforeEach
    void setUp() {
        // Setup Customer
        customer = new Customer();
        customer.setUsername("John Doe");
        customer.setPhoneNumber("0771234567");
        customer.setEmail("johndoe@gmail.com");

        // Setup Vehicle
        vehicle = new Vehicle();
        vehicle.setVehicleId(1L);
        vehicle.setRegistrationNo("ABC-123");
        vehicle.setVehicleType(VehicleType.CAR);
        vehicle.setModel("Toyota");
        vehicle.setCustomer(customer);

        // Setup ServiceItem
        serviceItem = new ServiceItem();
        serviceItem.setServiceItemId(1L);
        serviceItem.setServiceItemName("Oil Change");
        serviceItem.setEstimatedDuration(90);

        // Setup Appointment
        appointment = new Appointment();
        appointment.setAppointmentId(1L);
        appointment.setVehicle(vehicle);

        // Setup AppointmentJob
        appointmentJob = new AppointmentJob();
        appointmentJob.setAppointmentJobId(1L);
        appointmentJob.setJobStatus(Status.NEW);
        appointmentJob.setAppointment(appointment);
        appointmentJob.setServiceItem(serviceItem);

        // Setup job assignment list
        jobAssignmentList = List.of(
            new EmployeeJobAssignmentResponse(
                1L,
                "John Smith",
                null,null,null,null
            )
        );
    }

    @Test
    void getAppointmentJobByIdShouldReturnAppointmentJobSuccessfully() {
        // Given
        Long appointmentJobId = 1L;

        when(appointmentJobRepository.findById(appointmentJobId)).thenReturn(Optional.of(appointmentJob));
        when(jobAssignmentService.getJobAssignmentListByAppointmentJobId(appointmentJobId)).thenReturn(jobAssignmentList);

        // When
        EmployeeAppointmentJobResponse response = appointmentJobService.getAppointmentJobById(appointmentJobId);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getAppointmentJobId());
        assertNull(response.getJobNote());
        assertNull(response.getAdditionalCost());
        assertEquals("NEW", response.getJobStatus());

        // Verify vehicle details
        assertNotNull(response.getVehicle());
        assertEquals("ABC-123", response.getVehicle().getRegistrationNo());
        assertEquals("CAR", response.getVehicle().getVehicleType());
        assertEquals("Toyota", response.getVehicle().getVehicleModel());

        // Verify service item details
        assertNotNull(response.getServiceItem());
        assertEquals(1L, response.getServiceItem().getServiceItemId());
        assertEquals("Oil Change", response.getServiceItem().getServiceItemName());
        assertEquals(90, response.getServiceItem().getEstimatedDuration());

        // Verify customer details
        assertNotNull(response.getCustomer());
        assertEquals("John Doe", response.getCustomer().getUsername());
        assertEquals("0771234567", response.getCustomer().getPhoneNumber());
        assertEquals("johndoe@gmail.com", response.getCustomer().getEmail());

        // Verify job assignments
        assertNotNull(response.getJobAssignments());
        assertEquals(1, response.getJobAssignments().size());
        assertEquals(1L, response.getJobAssignments().getFirst().getEmployeeId());

        verify(appointmentJobRepository).findById(appointmentJobId);
        verify(jobAssignmentService).getJobAssignmentListByAppointmentJobId(appointmentJobId);
    }

    @Test
    void getAppointmentJobByIdShouldThrowExceptionWhenAppointmentJobNotFound() {
        // Given
        Long appointmentJobId = 999L;

        when(appointmentJobRepository.findById(appointmentJobId)).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(AppointmentJobNotFoundException.class, () ->
            appointmentJobService.getAppointmentJobById(appointmentJobId));

        assertEquals("Appointment Job Not Found. Job ID: " + appointmentJobId, exception.getMessage());

        verify(appointmentJobRepository).findById(appointmentJobId);
        verify(jobAssignmentService, never()).getJobAssignmentListByAppointmentJobId(any());
    }

    @Test
    void getAppointmentJobByIdShouldThrowExceptionWhenAppointmentIsNull() {
        // Given
        Long appointmentJobId = 1L;
        appointmentJob.setAppointment(null);

        when(appointmentJobRepository.findById(appointmentJobId)).thenReturn(Optional.of(appointmentJob));

        // When & Then
        Exception exception = assertThrows(DetailsMissingException.class, () ->
            appointmentJobService.getAppointmentJobById(appointmentJobId));

        assertEquals("Appointment not found for Appointment Job ID: " + appointmentJobId, exception.getMessage());

        verify(appointmentJobRepository).findById(appointmentJobId);
        verify(jobAssignmentService, never()).getJobAssignmentListByAppointmentJobId(any());
    }

    @Test
    void getAppointmentJobByIdShouldThrowExceptionWhenVehicleIsNull() {
        // Given
        Long appointmentJobId = 1L;
        appointment.setVehicle(null);

        when(appointmentJobRepository.findById(appointmentJobId)).thenReturn(Optional.of(appointmentJob));

        // When & Then
        Exception exception = assertThrows(DetailsMissingException.class, () ->
            appointmentJobService.getAppointmentJobById(appointmentJobId));

        assertEquals("Vehicle not found for Appointment ID: " + appointment.getAppointmentId(), exception.getMessage());

        verify(appointmentJobRepository).findById(appointmentJobId);
        verify(jobAssignmentService, never()).getJobAssignmentListByAppointmentJobId(any());
    }

    @Test
    void getAppointmentJobByIdShouldThrowExceptionWhenServiceItemIsNull() {
        // Given
        Long appointmentJobId = 1L;
        appointmentJob.setServiceItem(null);

        when(appointmentJobRepository.findById(appointmentJobId)).thenReturn(Optional.of(appointmentJob));

        // When & Then
        DetailsMissingException exception = assertThrows(DetailsMissingException.class, () ->
            appointmentJobService.getAppointmentJobById(appointmentJobId));

        assertEquals("Service Item not found for Appointment Job ID: " + appointmentJobId, exception.getMessage());

        verify(appointmentJobRepository).findById(appointmentJobId);
        verify(jobAssignmentService, never()).getJobAssignmentListByAppointmentJobId(any());
    }

    @Test
    void getAppointmentJobByIdShouldThrowExceptionWhenCustomerIsNull() {
        // Given
        Long appointmentJobId = 1L;
        vehicle.setCustomer(null);

        when(appointmentJobRepository.findById(appointmentJobId)).thenReturn(Optional.of(appointmentJob));

        // When & Then
        Exception exception = assertThrows(DetailsMissingException.class, () ->
            appointmentJobService.getAppointmentJobById(appointmentJobId));

        assertEquals("Customer details not found for the vehicle: " + vehicle.getVehicleId(), exception.getMessage());

        verify(appointmentJobRepository).findById(appointmentJobId);
        verify(jobAssignmentService, never()).getJobAssignmentListByAppointmentJobId(any());
    }

    // =============== UPDATE APPOINTMENT JOB STATUS TESTS ===============

    @Test
    void updateAppointmentJobStatusShouldUpdateStatusSuccessfully() {
        // Given
        Long appointmentJobId = 1L;
        String newStatus = "ONGOING";

        when(appointmentJobRepository.findById(appointmentJobId)).thenReturn(Optional.of(appointmentJob));
        when(appointmentJobRepository.save(any(AppointmentJob.class))).thenReturn(appointmentJob);

        // When
        String result = appointmentJobService.updateAppointmentJobStatus(appointmentJobId, newStatus);

        // Then
        assertEquals("Successfully updated Appointment Job to ONGOING", result);

        verify(appointmentJobRepository).findById(appointmentJobId);
        verify(appointmentJobRepository).save(argThat(job ->
            job.getJobStatus() == Status.ONGOING
        ));
    }

    @Test
    void updateAppointmentJobStatusShouldHandleLowercaseStatusInput() {
        // Given
        Long appointmentJobId = 1L;
        String newStatus = "completed";

        when(appointmentJobRepository.findById(appointmentJobId)).thenReturn(Optional.of(appointmentJob));
        when(appointmentJobRepository.save(any(AppointmentJob.class))).thenReturn(appointmentJob);

        // When
        String result = appointmentJobService.updateAppointmentJobStatus(appointmentJobId, newStatus);

        // Then
        assertEquals("Successfully updated Appointment Job to COMPLETED", result);

        verify(appointmentJobRepository).findById(appointmentJobId);
        verify(appointmentJobRepository).save(argThat(job ->
            job.getJobStatus() == Status.COMPLETED
        ));
    }

    @Test
    void updateAppointmentJobStatusShouldHandleMixedCaseStatusInput() {
        // Given
        Long appointmentJobId = 1L;
        String newStatus = "OnGoInG";

        when(appointmentJobRepository.findById(appointmentJobId)).thenReturn(Optional.of(appointmentJob));
        when(appointmentJobRepository.save(any(AppointmentJob.class))).thenReturn(appointmentJob);

        // When
        String result = appointmentJobService.updateAppointmentJobStatus(appointmentJobId, newStatus);

        // Then
        assertEquals("Successfully updated Appointment Job to ONGOING", result);

        verify(appointmentJobRepository).save(argThat(job ->
            job.getJobStatus() == Status.ONGOING
        ));
    }

    @Test
    void updateAppointmentJobStatusShouldThrowExceptionWhenAppointmentJobNotFound() {
        // Given
        Long appointmentJobId = 999L;
        String newStatus = "COMPLETED";

        when(appointmentJobRepository.findById(appointmentJobId)).thenReturn(Optional.empty());

        // When & Then
        AppointmentJobNotFoundException exception = assertThrows(AppointmentJobNotFoundException.class, () ->
            appointmentJobService.updateAppointmentJobStatus(appointmentJobId, newStatus));

        assertEquals("Appointment Job Not Found. Job ID: " + appointmentJobId, exception.getMessage());

        verify(appointmentJobRepository).findById(appointmentJobId);
        verify(appointmentJobRepository, never()).save(any(AppointmentJob.class));
    }

    @Test
    void updateAppointmentJobStatusShouldThrowExceptionForNullStatus() {
        // Given
        Long appointmentJobId = 1L;
        String newStatus = null;

        when(appointmentJobRepository.findById(appointmentJobId)).thenReturn(Optional.of(appointmentJob));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            appointmentJobService.updateAppointmentJobStatus(appointmentJobId, newStatus));

        assertEquals("Invalid Job Status: null. Valid values are: NEW, ONGOING, COMPLETED", exception.getMessage());

        verify(appointmentJobRepository).findById(appointmentJobId);
        verify(appointmentJobRepository, never()).save(any(AppointmentJob.class));
    }

    @Test
    void updateAppointmentJobStatusShouldThrowExceptionForInvalidStatus() {
        // Given
        Long appointmentJobId = 1L;
        String invalidStatus = "INVALID_STATUS";

        when(appointmentJobRepository.findById(appointmentJobId)).thenReturn(Optional.of(appointmentJob));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            appointmentJobService.updateAppointmentJobStatus(appointmentJobId, invalidStatus));

        assertTrue(exception.getMessage().contains("Invalid Job Status: " + invalidStatus));
        assertTrue(exception.getMessage().contains("Valid values are: NEW, ONGOING, COMPLETED"));

        verify(appointmentJobRepository).findById(appointmentJobId);
        verify(appointmentJobRepository, never()).save(any(AppointmentJob.class));
    }

    @Test
    void updateAppointmentJobStatusShouldThrowExceptionForEmptyStatus() {
        // Given
        Long appointmentJobId = 1L;
        String emptyStatus = "";

        when(appointmentJobRepository.findById(appointmentJobId)).thenReturn(Optional.of(appointmentJob));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            appointmentJobService.updateAppointmentJobStatus(appointmentJobId, emptyStatus));

        assertTrue(exception.getMessage().contains("Invalid Job Status: "));
        assertTrue(exception.getMessage().contains("Valid values are: NEW, ONGOING, COMPLETED"));

        verify(appointmentJobRepository).findById(appointmentJobId);
        verify(appointmentJobRepository, never()).save(any(AppointmentJob.class));
    }


    @Test
    void updateAppointmentJobStatusShouldThrowExceptionWhenRepositorySaveFails() {
        // Given
        Long appointmentJobId = 1L;
        String newStatus = "COMPLETED";

        when(appointmentJobRepository.findById(appointmentJobId)).thenReturn(Optional.of(appointmentJob));
        when(appointmentJobRepository.save(any(AppointmentJob.class)))
            .thenThrow(new RuntimeException("Database connection error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            appointmentJobService.updateAppointmentJobStatus(appointmentJobId, newStatus));

        assertTrue(exception.getMessage().contains("Failed to update Appointment Job Status"));

        verify(appointmentJobRepository).findById(appointmentJobId);
        verify(appointmentJobRepository).save(any(AppointmentJob.class));
    }



}