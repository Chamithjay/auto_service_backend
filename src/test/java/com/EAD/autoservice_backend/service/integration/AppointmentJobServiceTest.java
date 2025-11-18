package com.EAD.autoservice_backend.service.integration;

import com.EAD.autoservice_backend.dto.EmployeeAppointmentJobResponse;
import com.EAD.autoservice_backend.exception.AppointmentJobNotFoundException;
import com.EAD.autoservice_backend.exception.DetailsMissingException;
import com.EAD.autoservice_backend.model.*;
import com.EAD.autoservice_backend.repository.*;
import com.EAD.autoservice_backend.service.AppointmentJobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AppointmentJobServiceTest {

    @Autowired
    private AppointmentJobService appointmentJobService;

    @Autowired
    private AppointmentJobRepository appointmentJobRepository;


    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceItemRepository serviceItemRepository;

    private Customer testCustomer;
    private Vehicle testVehicle;
    private ServiceItem testServiceItem;
    private Appointment testAppointment;
    private AppointmentJob testAppointmentJob;

    @BeforeEach
    void setup() {

        testCustomer = new Customer();
        testCustomer.setUsername("John");
        testCustomer.setEmail("john@gmail.com");
        testCustomer.setPassword("john123");
        testCustomer.setPhoneNumber("0771234567");
        testCustomer = userRepository.save(testCustomer);

        testVehicle = new Vehicle();
        testVehicle.setRegistrationNo("ABC-123");
        testVehicle.setVehicleType(VehicleType.CAR);
        testVehicle.setModel("Toyota");
        testVehicle.setVehicleName("Office car");
        testVehicle.setCustomer(testCustomer);
        testVehicle = vehicleRepository.save(testVehicle);

        testServiceItem = new ServiceItem();
        testServiceItem.setServiceItemName("Oil Change Service");
        testServiceItem.setEstimatedDuration(90);
        testServiceItem.setRequiredEmployeeCount(2);
        testServiceItem.setServiceItemCost(new BigDecimal(2000.00));
        testServiceItem.setVehicleType(VehicleType.CAR);
        testServiceItem.setServiceItemType(ServiceItemType.SERVICE);
        testServiceItem = serviceItemRepository.save(testServiceItem);

        testAppointment = new Appointment();
        testAppointment.setCustomer(testCustomer);
        testAppointment.setVehicleName("Office car");
        testAppointment.setVehicle(testVehicle);
        testAppointment.setAppointmentDate(LocalDate.now().plusDays(2));
        testAppointment.setAppointmentStartTime(LocalDateTime.now());
        testAppointment.setAppointmentEndTime(LocalDateTime.now().plusHours(2));
        testAppointment.setStatus(AppointmentStatus.NEW);
        testAppointment.setTotalCost(new BigDecimal(2000.00));
        testAppointment.setSessionType(SessionType.MORNING);
        testAppointment = appointmentRepository.save(testAppointment);

        testAppointmentJob = new AppointmentJob();
        testAppointmentJob.setItemStatus(AppointmentStatus.NEW);
        testAppointmentJob.setAppointment(testAppointment);
        testAppointmentJob.setServiceItem(testServiceItem);
        testAppointmentJob = appointmentJobRepository.save(testAppointmentJob);

    }

    @Test
    @DisplayName("Should retrieve appointment job by ID successfully - integration test")
    void shouldRetrieveAppointmentJobByIdSuccessfully() {
        // When

        EmployeeAppointmentJobResponse response = appointmentJobService.getAppointmentJobById(testAppointmentJob.getId());

        //Then
        assertThat(response).isNotNull();
        assertThat(response.getAppointmentJobId()).isEqualTo(testAppointmentJob.getId());
        assertThat(response.getJobNote()).isNull();
        assertThat(response.getJobStatus()).isEqualTo("NEW");
        assertThat(response.getAdditionalCost()).isNull();

        // Verify vehicle details
        assertThat(response.getVehicle()).isNotNull();
        assertThat(response.getVehicle().getRegistrationNo()).isEqualTo("ABC-123");
        assertThat(response.getVehicle().getVehicleType()).isEqualTo("CAR");
        assertThat(response.getVehicle().getVehicleModel()).isEqualTo("Toyota");

        // Verify service item details
        assertThat(response.getServiceItem()).isNotNull();
        assertThat(response.getServiceItem().getServiceItemId()).isEqualTo(testServiceItem.getServiceItemId());
        assertThat(response.getServiceItem().getServiceItemName()).isEqualTo("Oil Change Service");
        assertThat(response.getServiceItem().getEstimatedDuration()).isEqualTo(90);

        // Verify customer details
        assertThat(response.getCustomer()).isNotNull();
        assertThat(response.getCustomer().getUsername()).isEqualTo(testCustomer.getUsername());
        assertThat(response.getCustomer().getPhoneNumber()).isEqualTo("0771234567");
        assertThat(response.getCustomer().getEmail()).isEqualTo("john@gmail.com");


        // Verify job assignments
        assertThat(response.getJobAssignments()).isNotNull();

    }


    @Test
    @DisplayName("Should throw AppointmentJobNotFoundException when job does not exist - integration test")
    void shouldThrowExceptionWhenAppointmentJobNotFound() {
        // Given
        Long nonExistentJobId = 9999L;

        // When & Then
        assertThatThrownBy(() -> appointmentJobService.getAppointmentJobById(nonExistentJobId))
                .isInstanceOf(AppointmentJobNotFoundException.class)
                .hasMessage("Appointment Job Not Found. Job ID: 9999");

        assertThat(appointmentJobRepository.findById(testAppointmentJob.getId())).isPresent();
    }


    // ==================== Job Status Update Tests ==================== //

    @Test
    @DisplayName("Should update appointment job status to ONGOING successfully - integration test")
    void shouldUpdateAppointmentJobStatusToOngoingSuccessfully() {
        // When
        String result = appointmentJobService.updateAppointmentJobStatus(testAppointmentJob.getId(), "ONGOING");

        // Then
        assertThat(result).isEqualTo("Successfully updated Appointment Job to ONGOING");

        // Verify in database
        AppointmentJob updatedJob = appointmentJobRepository.findById(testAppointmentJob.getId()).orElse(null);
        assertThat(updatedJob).isNotNull();
        assertThat(updatedJob.getItemStatus()).isEqualTo(AppointmentStatus.ONGOING);
    }


    @Test
    @DisplayName("Should update appointment job status to COMPLETED successfully - integration test")
    void shouldUpdateAppointmentJobStatusToCompletedSuccessfully() {
        // When
        String result = appointmentJobService.updateAppointmentJobStatus(testAppointmentJob.getId(), "COMPLETED");

        // Then
        assertThat(result).isEqualTo("Successfully updated Appointment Job to COMPLETED");

        // Verify in database
        AppointmentJob updatedJob = appointmentJobRepository.findById(testAppointmentJob.getId()).orElse(null);
        assertThat(updatedJob).isNotNull();
        assertThat(updatedJob.getItemStatus()).isEqualTo(AppointmentStatus.COMPLETED);
    }

    @Test
    @DisplayName("Should handle lowercase status input - integration test")
    void shouldHandleLowercaseStatusInput() {
        // When
        String result = appointmentJobService.updateAppointmentJobStatus(testAppointmentJob.getId(), "ongoing");

        // Then
        assertThat(result).isEqualTo("Successfully updated Appointment Job to ONGOING");

        // Verify in database
        AppointmentJob updatedJob = appointmentJobRepository.findById(testAppointmentJob.getId()).orElse(null);
        assertThat(updatedJob).isNotNull();
        assertThat(updatedJob.getItemStatus()).isEqualTo(AppointmentStatus.ONGOING);
    }

    @Test
    @DisplayName("Should handle mixed case status input - integration test")
    void shouldHandleMixedCaseStatusInput() {
        // When
        String result = appointmentJobService.updateAppointmentJobStatus(testAppointmentJob.getId(), "CoMpLeTeD");

        // Then
        assertThat(result).isEqualTo("Successfully updated Appointment Job to COMPLETED");

        // Verify in database
        AppointmentJob updatedJob = appointmentJobRepository.findById(testAppointmentJob.getId()).orElse(null);
        assertThat(updatedJob).isNotNull();
        assertThat(updatedJob.getItemStatus()).isEqualTo(AppointmentStatus.COMPLETED);
    }


    @Test
    @DisplayName("Should throw AppointmentJobNotFoundException for non-existent job ID - integration test")
    void shouldThrowExceptionForNonExistentJobId() {
        // Given
        Long nonExistentJobId = 99999L;

        // When & Then
        assertThatThrownBy(() -> appointmentJobService.updateAppointmentJobStatus(nonExistentJobId, "COMPLETED"))
                .isInstanceOf(AppointmentJobNotFoundException.class)
                .hasMessage("Appointment Job Not Found. Job ID: 99999");

        // Verify original job is unchanged
        AppointmentJob originalJob = appointmentJobRepository.findById(testAppointmentJob.getId()).orElse(null);
        assertThat(originalJob).isNotNull();
        assertThat(originalJob.getItemStatus()).isEqualTo(AppointmentStatus.NEW);

    }


    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid status - integration test")
    void shouldThrowExceptionForInvalidStatus() {
        // Given
        String invalidStatus = "INVALID_STATUS";

        // When & Then
        assertThatThrownBy(() -> appointmentJobService.updateAppointmentJobStatus(testAppointmentJob.getId(), invalidStatus))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid Job Status: INVALID_STATUS")
                .hasMessageContaining("Valid values are: NEW, ONGOING, COMPLETED");

        // Verify original job is unchanged
        AppointmentJob originalJob = appointmentJobRepository.findById(testAppointmentJob.getId()).orElse(null);
        assertThat(originalJob).isNotNull();
        assertThat(originalJob.getItemStatus()).isEqualTo(AppointmentStatus.NEW);

    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for empty status - integration test")
    void shouldThrowExceptionForEmptyStatus() {
        // When & Then
        assertThatThrownBy(() -> appointmentJobService.updateAppointmentJobStatus(testAppointmentJob.getId(), ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid Job Status:")
                .hasMessageContaining("Valid values are: NEW, ONGOING, COMPLETED");

        // Verify original job is unchanged
        AppointmentJob originalJob = appointmentJobRepository.findById(testAppointmentJob.getId()).orElse(null);
        assertThat(originalJob).isNotNull();
        assertThat(originalJob.getItemStatus()).isEqualTo(AppointmentStatus.NEW);

    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for null status - integration test")
    void shouldThrowExceptionForNullStatus() {
        // When & Then
        assertThatThrownBy(() -> appointmentJobService.updateAppointmentJobStatus(testAppointmentJob.getId(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid Job Status: null")
                .hasMessageContaining("Valid values are: NEW, ONGOING, COMPLETED");

        // Verify original job is unchanged
        AppointmentJob originalJob = appointmentJobRepository.findById(testAppointmentJob.getId()).orElse(null);
        assertThat(originalJob).isNotNull();
        assertThat(originalJob.getItemStatus()).isEqualTo(AppointmentStatus.NEW);
    }
}
