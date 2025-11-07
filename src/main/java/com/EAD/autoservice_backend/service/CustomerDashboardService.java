package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.AppointmentProgressResponse;
import com.EAD.autoservice_backend.dto.AppointmentSummaryResponse;
import com.EAD.autoservice_backend.dto.CustomerDashboardStatsResponse;
import com.EAD.autoservice_backend.exception.ResourceNotFoundException;
import com.EAD.autoservice_backend.model.*;
import com.EAD.autoservice_backend.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for customer dashboard operations
 */
@Service
@Transactional(readOnly = true)
public class CustomerDashboardService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerDashboardService.class);

    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentJobRepository appointmentJobRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Autowired
    public CustomerDashboardService(CustomerRepository customerRepository,
                                    VehicleRepository vehicleRepository,
                                    AppointmentRepository appointmentRepository,
                                    AppointmentJobRepository appointmentJobRepository) {
        this.customerRepository = customerRepository;
        this.vehicleRepository = vehicleRepository;
        this.appointmentRepository = appointmentRepository;
        this.appointmentJobRepository = appointmentJobRepository;
    }

    /**
     * Get dashboard statistics for a customer
     */
    public CustomerDashboardStatsResponse getDashboardStats(String username) {
        try {
            logger.info("Fetching dashboard stats for user: {}", username);

            Customer customer = customerRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with username: " + username));

            logger.debug("Found customer with ID: {}", customer.getId());

            Integer totalVehicles = vehicleRepository.countByCustomerId(customer.getId());
            logger.debug("Total vehicles: {}", totalVehicles);

            Integer totalAppointments = appointmentRepository.countByCustomerId(customer.getId());
            logger.debug("Total appointments: {}", totalAppointments);

            List<AppointmentStatus> activeStatuses = Arrays.asList(AppointmentStatus.NEW, AppointmentStatus.ONGOING);
            Integer activeAppointments = appointmentRepository.countByCustomerIdAndStatusIn(
                    customer.getId(), activeStatuses
            );
            logger.debug("Active appointments: {}", activeAppointments);

            Integer completedAppointments = appointmentRepository.countByCustomerIdAndStatus(
                    customer.getId(), AppointmentStatus.COMPLETED
            );
            logger.debug("Completed appointments: {}", completedAppointments);

            return new CustomerDashboardStatsResponse(
                    totalVehicles != null ? totalVehicles : 0,
                    totalAppointments != null ? totalAppointments : 0,
                    activeAppointments != null ? activeAppointments : 0,
                    completedAppointments != null ? completedAppointments : 0
            );
        } catch (ResourceNotFoundException e) {
            logger.error("Customer not found: {}", username);
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching dashboard stats for user {}: {}", username, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch dashboard statistics: " + e.getMessage(), e);
        }
    }

    /**
     * Get all appointments for a customer with summary information
     */
    public List<AppointmentSummaryResponse> getCustomerAppointments(String username) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with username: " + username));

        List<Appointment> appointments = appointmentRepository.findByCustomerId(customer.getId());

        return appointments.stream()
                .map(this::mapToAppointmentSummary)
                .collect(Collectors.toList());
    }

    /**
     * Get active appointments only (NEW or ONGOING)
     */
    public List<AppointmentSummaryResponse> getActiveAppointments(String username) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with username: " + username));

        List<AppointmentStatus> activeStatuses = Arrays.asList(AppointmentStatus.NEW, AppointmentStatus.ONGOING);
        List<Appointment> appointments = appointmentRepository.findActiveAppointmentsByCustomerId(
                customer.getId(), activeStatuses
        );

        return appointments.stream()
                .map(this::mapToAppointmentSummary)
                .collect(Collectors.toList());
    }

    /**
     * Get detailed progress information for a specific appointment
     */
    public AppointmentProgressResponse getAppointmentProgress(String username, Long appointmentId) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with username: " + username));

        Appointment appointment = appointmentRepository.findByAppointmentIdAndCustomerId(
                        appointmentId, customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        return mapToAppointmentProgress(appointment);
    }

    /**
     * Map Appointment to AppointmentSummaryResponse
     */
    private AppointmentSummaryResponse mapToAppointmentSummary(Appointment appointment) {
        try {
            Integer totalJobs = appointmentJobRepository.countByAppointmentId(appointment.getAppointmentId());
            Integer completedJobs = appointmentJobRepository.countByAppointmentIdAndJobStatus(
                    appointment.getAppointmentId(), AppointmentStatus.COMPLETED
            );

            Vehicle vehicle = appointment.getVehicle();
            AppointmentSummaryResponse.VehicleBasicInfo vehicleInfo;

            if (vehicle == null) {
                logger.warn("No vehicle found for appointment ID: {}. Using fallback vehicle data from appointment.",
                           appointment.getAppointmentId());
                // Use vehicle data stored in the appointment itself as fallback
                vehicleInfo = new AppointmentSummaryResponse.VehicleBasicInfo(
                        null,  // No vehicle ID available
                        appointment.getVehicleName() != null ? appointment.getVehicleName() : "Unknown Vehicle",
                        "N/A"  // No registration number available
                );
            } else {
                vehicleInfo = new AppointmentSummaryResponse.VehicleBasicInfo(
                        vehicle.getVehicleId(),
                        vehicle.getVehicleName(),
                        vehicle.getRegistrationNo()
                );
            }

            // Handle null timestamps
            String startTime = appointment.getAppointmentStartTime() != null
                ? appointment.getAppointmentStartTime().toLocalTime().format(TIME_FORMATTER)
                : "N/A";
            String endTime = appointment.getAppointmentEndTime() != null
                ? appointment.getAppointmentEndTime().toLocalTime().format(TIME_FORMATTER)
                : "N/A";

            return new AppointmentSummaryResponse(
                    appointment.getAppointmentId(),
                    appointment.getAppointmentDate().format(DATE_FORMATTER),
                    startTime,
                    endTime,
                    appointment.getStatus(),
                    appointment.getTotalCost(),
                    vehicleInfo,
                    totalJobs != null ? totalJobs : 0,
                    completedJobs != null ? completedJobs : 0
            );
        } catch (Exception e) {
            logger.error("Error mapping appointment {}: {}", appointment.getAppointmentId(), e.getMessage(), e);
            throw new RuntimeException("Failed to map appointment data: " + e.getMessage(), e);
        }
    }

    /**
     * Map Appointment to detailed AppointmentProgressResponse
     */
    private AppointmentProgressResponse mapToAppointmentProgress(Appointment appointment) {
        AppointmentProgressResponse response = new AppointmentProgressResponse();

        response.setAppointmentId(appointment.getAppointmentId());
        response.setAppointmentDate(appointment.getAppointmentDate().format(DATE_FORMATTER));

        // Handle null timestamps
        String startTime = appointment.getAppointmentStartTime() != null
            ? appointment.getAppointmentStartTime().toLocalTime().format(TIME_FORMATTER)
            : "N/A";
        String endTime = appointment.getAppointmentEndTime() != null
            ? appointment.getAppointmentEndTime().toLocalTime().format(TIME_FORMATTER)
            : "N/A";

        response.setStartTime(startTime);
        response.setEndTime(endTime);
        response.setStatus(appointment.getStatus());
        response.setTotalCost(appointment.getTotalCost());

        // Map vehicle information
        Vehicle vehicle = appointment.getVehicle();
        AppointmentProgressResponse.VehicleInfo vehicleInfo = new AppointmentProgressResponse.VehicleInfo();

        if (vehicle == null) {
            logger.warn("No vehicle found for appointment ID: {} in progress details. Using fallback data.",
                       appointment.getAppointmentId());
            vehicleInfo.setVehicleId(null);
            vehicleInfo.setVehicleName(appointment.getVehicleName() != null ? appointment.getVehicleName() : "Unknown Vehicle");
            vehicleInfo.setRegistrationNo("N/A");
            vehicleInfo.setVehicleType("N/A");
            vehicleInfo.setModel("N/A");
        } else {
            vehicleInfo.setVehicleId(vehicle.getVehicleId());
            vehicleInfo.setVehicleName(vehicle.getVehicleName());
            vehicleInfo.setRegistrationNo(vehicle.getRegistrationNo());
            vehicleInfo.setVehicleType(vehicle.getVehicleType().name());
            vehicleInfo.setModel(vehicle.getModel());
        }
        response.setVehicle(vehicleInfo);

        // Map job information
        List<AppointmentJob> jobs = appointmentJobRepository.findByAppointmentId(appointment.getAppointmentId());
        List<AppointmentProgressResponse.JobProgressInfo> jobInfos = jobs.stream()
                .map(this::mapToJobProgressInfo)
                .collect(Collectors.toList());
        response.setJobs(jobInfos);

        return response;
    }

    /**
     * Map AppointmentJob to JobProgressInfo
     */
    private AppointmentProgressResponse.JobProgressInfo mapToJobProgressInfo(AppointmentJob job) {
        AppointmentProgressResponse.JobProgressInfo jobInfo = new AppointmentProgressResponse.JobProgressInfo();

        jobInfo.setAppointmentJobId(job.getId());
        jobInfo.setServiceItemName(job.getServiceItem().getServiceItemName());
        jobInfo.setServiceItemType(job.getServiceItem().getServiceItemType().name());
        jobInfo.setJobStatus(job.getItemStatus());
        jobInfo.setStartTime(job.getStartTime() != null ? job.getStartTime().toString() : null);
        jobInfo.setEndTime(job.getEndTime() != null ? job.getEndTime().toString() : null);
        jobInfo.setAdditionalCost(job.getAdditionalCost());
        jobInfo.setJobNote(job.getJobNote());

        // Count assigned employees
        Integer assignedEmployees = job.getEmployeeAssignments() != null ?
                job.getEmployeeAssignments().size() : 0;
        jobInfo.setAssignedEmployees(assignedEmployees);

        return jobInfo;
    }
}
