package com.EAD.autoservice_backend.service.unit;

import com.EAD.autoservice_backend.dto.EmployeeAppointmentJobResponse;
import com.EAD.autoservice_backend.dto.EmployeeCustomerDetailsResponse;
import com.EAD.autoservice_backend.dto.EmployeeJobAssignmentResponse;
import com.EAD.autoservice_backend.dto.EmployeeJobNoteRequest;
import com.EAD.autoservice_backend.dto.EmployeeServiceItemResponse;
import com.EAD.autoservice_backend.dto.EmployeeVehicleResponse;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentJobServiceTest {

    @Mock
    private AppointmentJobRepository appointmentJobRepository;

    @Mock
    private JobAssignmentService jobAssignmentService;

    @InjectMocks
    private AppointmentJobService appointmentJobService;

    // Reusable entities
    private Vehicle vehicle;
    private Customer customer;
    private Appointment appointment;
    private ServiceItem serviceItem;

    @BeforeEach
    void setUp() {
        // Customer
        customer = new Customer();
        customer.setUsername("John Doe");
        customer.setEmail("johndoe@gmail.com");
        customer.setPhoneNumber("0771234567");

        // Vehicle
        vehicle = new Vehicle();
        vehicle.setVehicleId(10L);
        vehicle.setVehicleName("Toyota Aqua");
        vehicle.setRegistrationNo("ABC-1234");
        vehicle.setVehicleType(VehicleType.CAR);
        vehicle.setModel("Toyota");
        vehicle.setCustomer(customer);

        // Appointment
        appointment = Appointment.builder()
                .appointmentId(100L)
                .customer(customer)
                .vehicle(vehicle)
                .vehicleName("Toyota Aqua")
                .appointmentDate(LocalDate.now())
                .appointmentStartTime(LocalDateTime.now())
                .appointmentEndTime(LocalDateTime.now().plusMinutes(60))
                .sessionType(SessionType.EVENING)
                .status(AppointmentStatus.NEW)
                .totalCost(new BigDecimal("10000.00"))
                .build();

        // ServiceItem
        serviceItem = new ServiceItem();
        serviceItem.setServiceItemId(5L);
        serviceItem.setServiceItemName("Oil Change");
        serviceItem.setVehicleType(VehicleType.CAR);
        serviceItem.setRequiredEmployeeCount(1);
        serviceItem.setServiceItemCost(new BigDecimal("3500.00"));
        serviceItem.setServiceItemType(ServiceItemType.SERVICE);
        serviceItem.setEstimatedDuration(90);
    }

    private AppointmentJob buildJob(Long id) {
        AppointmentJob job = new AppointmentJob();
        job.setId(id);
        job.setAppointment(appointment);
        job.setServiceItem(serviceItem);
        job.setItemStatus(AppointmentStatus.NEW);
        // if your entity uses `additional_cost` Lombok will still give a getter,
        // but we don't rely on it explicitly in assertions.
        return job;
    }

    // -------- getAppointmentJobById --------

    @Test
    void getAppointmentJobById_success() {
        AppointmentJob job = buildJob(1L);

        when(appointmentJobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(jobAssignmentService.getJobAssignmentListByAppointmentJobId(1L))
                .thenReturn(List.of(new EmployeeJobAssignmentResponse(1L, "Tech A", null, null, null, null)));

        EmployeeAppointmentJobResponse resp = appointmentJobService.getAppointmentJobById(1L);

        assertNotNull(resp);
        assertEquals(1L, resp.getAppointmentJobId());
        assertNull(resp.getJobNote());                 // no note set yet
        assertEquals("NEW", resp.getJobStatus());

        EmployeeVehicleResponse v = resp.getVehicle();
        assertEquals("ABC-1234", v.getRegistrationNo());
        assertEquals("CAR", v.getVehicleType());
        assertEquals("Toyota", v.getVehicleModel());

        EmployeeServiceItemResponse si = resp.getServiceItem();
        assertEquals(5L, si.getServiceItemId());
        assertEquals("Oil Change", si.getServiceItemName());
        assertEquals(90, si.getEstimatedDuration());

        EmployeeCustomerDetailsResponse c = resp.getCustomer();
        assertEquals("John Doe", c.getUsername());
        assertEquals("0771234567", c.getPhoneNumber());
        assertEquals("johndoe@gmail.com", c.getEmail());

        assertEquals(1, resp.getJobAssignments().size());

        verify(appointmentJobRepository).findById(1L);
        verify(jobAssignmentService).getJobAssignmentListByAppointmentJobId(1L);
    }

    @Test
    void getAppointmentJobById_notFound_throws() {
        when(appointmentJobRepository.findById(999L)).thenReturn(Optional.empty());
        AppointmentJobNotFoundException ex = assertThrows(
                AppointmentJobNotFoundException.class,
                () -> appointmentJobService.getAppointmentJobById(999L)
        );
        assertEquals("Appointment Job Not Found. Job ID: 999", ex.getMessage());
        verify(jobAssignmentService, never()).getJobAssignmentListByAppointmentJobId(anyLong());
    }

    @Test
    void getAppointmentJobById_missingAppointment_throws() {
        AppointmentJob job = buildJob(1L);
        job.setAppointment(null);

        when(appointmentJobRepository.findById(1L)).thenReturn(Optional.of(job));

        DetailsMissingException ex = assertThrows(
                DetailsMissingException.class,
                () -> appointmentJobService.getAppointmentJobById(1L)
        );
        assertEquals("Appointment not found for Appointment Job ID: 1", ex.getMessage());
    }

    @Test
    void getAppointmentJobById_missingVehicle_throws() {
        AppointmentJob job = buildJob(1L);
        Appointment appt = appointment;
        appt.setVehicle(null); // break vehicle
        job.setAppointment(appt);

        when(appointmentJobRepository.findById(1L)).thenReturn(Optional.of(job));

        DetailsMissingException ex = assertThrows(
                DetailsMissingException.class,
                () -> appointmentJobService.getAppointmentJobById(1L)
        );
        assertEquals("Vehicle not found for Appointment ID: 100", ex.getMessage());
    }

    @Test
    void getAppointmentJobById_missingServiceItem_throws() {
        AppointmentJob job = buildJob(1L);
        job.setServiceItem(null);

        when(appointmentJobRepository.findById(1L)).thenReturn(Optional.of(job));

        DetailsMissingException ex = assertThrows(
                DetailsMissingException.class,
                () -> appointmentJobService.getAppointmentJobById(1L)
        );
        assertEquals("Service Item not found for Appointment Job ID: 1", ex.getMessage());
    }

    @Test
    void getAppointmentJobById_missingCustomer_throws() {
        AppointmentJob job = buildJob(1L);
        vehicle.setCustomer(null); // remove customer from vehicle

        when(appointmentJobRepository.findById(1L)).thenReturn(Optional.of(job));

        DetailsMissingException ex = assertThrows(
                DetailsMissingException.class,
                () -> appointmentJobService.getAppointmentJobById(1L)
        );
        assertEquals("Customer details not found for the vehicle: 10", ex.getMessage());
    }

    // -------- updateAppointmentJobStatus --------

    @Test
    void updateAppointmentJobStatus_updatesToOngoing() {
        AppointmentJob job = buildJob(1L);

        when(appointmentJobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(appointmentJobRepository.save(job)).thenReturn(job);

        String msg = appointmentJobService.updateAppointmentJobStatus(1L, "ONGOING");

        assertEquals("Successfully updated Appointment Job to ONGOING", msg);
        assertEquals(AppointmentStatus.ONGOING, job.getItemStatus());
        verify(appointmentJobRepository).save(job);
    }

    @Test
    void updateAppointmentJobStatus_handlesCaseInsensitivity() {
        AppointmentJob job = buildJob(1L);

        when(appointmentJobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(appointmentJobRepository.save(job)).thenReturn(job);

        String msg = appointmentJobService.updateAppointmentJobStatus(1L, "completed");

        assertEquals("Successfully updated Appointment Job to COMPLETED", msg);
        assertEquals(AppointmentStatus.COMPLETED, job.getItemStatus());
        verify(appointmentJobRepository).save(job);
    }

    @Test
    void updateAppointmentJobStatus_invalidStatus_throws() {
        AppointmentJob job = buildJob(1L);
        when(appointmentJobRepository.findById(1L)).thenReturn(Optional.of(job));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> appointmentJobService.updateAppointmentJobStatus(1L, "INVALID_X")
        );
        assertTrue(ex.getMessage().startsWith("Invalid Job Status: INVALID_X"));
        verify(appointmentJobRepository, never()).save(any());
    }

    @Test
    void updateAppointmentJobStatus_notFound_throws() {
        when(appointmentJobRepository.findById(999L)).thenReturn(Optional.empty());
        AppointmentJobNotFoundException ex = assertThrows(
                AppointmentJobNotFoundException.class,
                () -> appointmentJobService.updateAppointmentJobStatus(999L, "COMPLETED")
        );
        assertEquals("Appointment Job Not Found. Job ID: 999", ex.getMessage());
    }

    // -------- saveJobNoteForAppointmentJob --------

    @Test
    void saveJobNote_firstNote_appendsPeriod() {
        AppointmentJob job = buildJob(1L);
        job.setJobNote(null);

        when(appointmentJobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(appointmentJobRepository.save(job)).thenReturn(job);
        when(jobAssignmentService.getJobAssignmentListByAppointmentJobId(1L))
                .thenReturn(List.of()); // the service calls getAppointmentJobById at the end

        EmployeeJobNoteRequest req = new EmployeeJobNoteRequest("Initial note");


        EmployeeAppointmentJobResponse resp = appointmentJobService.saveJobNoteForAppointmentJob(1L, req);

        assertEquals("Initial note.", resp.getJobNote());
        assertEquals("Initial note.", job.getJobNote());
        verify(appointmentJobRepository).save(job);
    }

    @Test
    void saveJobNote_existingNote_appendsWithNewLineAndPeriod() {
        AppointmentJob job = buildJob(1L);
        job.setJobNote("Old note.");

        when(appointmentJobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(appointmentJobRepository.save(job)).thenReturn(job);
        when(jobAssignmentService.getJobAssignmentListByAppointmentJobId(1L))
                .thenReturn(List.of());

        EmployeeJobNoteRequest req = new EmployeeJobNoteRequest("New info");


        EmployeeAppointmentJobResponse resp = appointmentJobService.saveJobNoteForAppointmentJob(1L, req);

        assertEquals("Old note.\nNew info.", resp.getJobNote());
        assertEquals("Old note.\nNew info.", job.getJobNote());
        verify(appointmentJobRepository).save(job);
    }

    @Test
    void saveJobNote_blank_throws() {
        AppointmentJob job = buildJob(1L);

        when(appointmentJobRepository.findById(1L)).thenReturn(Optional.of(job));

        EmployeeJobNoteRequest req = new EmployeeJobNoteRequest("  ");


        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> appointmentJobService.saveJobNoteForAppointmentJob(1L, req)
        );
        assertEquals("Job note cannot be empty or blank", ex.getMessage());
        verify(appointmentJobRepository, never()).save(any());
    }

    @Test
    void saveJobNote_notFound_throws() {
        when(appointmentJobRepository.findById(123L)).thenReturn(Optional.empty());

        EmployeeJobNoteRequest req = new EmployeeJobNoteRequest("Note ");
        ;

        assertThrows(AppointmentJobNotFoundException.class,
                () -> appointmentJobService.saveJobNoteForAppointmentJob(123L, req));
    }
}
