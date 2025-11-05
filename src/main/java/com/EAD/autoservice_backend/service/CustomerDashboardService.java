//package com.EAD.autoservice_backend.service;
//
//import com.EAD.autoservice_backend.dto.*;
//import com.EAD.autoservice_backend.dto.Customer.*;
//import com.EAD.autoservice_backend.exception.ResourceNotFoundException;
//import com.EAD.autoservice_backend.model.*;
//import com.EAD.autoservice_backend.repository.*;
//import com.EAD.autoservice_backend.repository.Customer.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.format.DateTimeFormatter;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * Service for customer dashboard operations
// */
//@Service
//@Transactional(readOnly = true)
//public class CustomerDashboardService {
//
//    private final CustomerRepository customerRepository;
//    private final VehicleRepository vehicleRepository;
//    private final AppointmentRepository appointmentRepository;
//    private final AppointmentJobRepository appointmentJobRepository;
//
//    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
//    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//    @Autowired
//    public CustomerDashboardService(CustomerRepository customerRepository,
//                                   VehicleRepository vehicleRepository,
//                                   AppointmentRepository appointmentRepository,
//                                   AppointmentJobRepository appointmentJobRepository) {
//        this.customerRepository = customerRepository;
//        this.vehicleRepository = vehicleRepository;
//        this.appointmentRepository = appointmentRepository;
//        this.appointmentJobRepository = appointmentJobRepository;
//    }
//
//    /**
//     * Get dashboard statistics for a customer
//     */
//    public CustomerDashboardStatsResponse getDashboardStats(String username) {
//        Customer customer = customerRepository.findByUsername(username)
//                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with username: " + username));
//
//        Integer totalVehicles = vehicleRepository.countByCustomerId(customer.getId());
//        Integer totalAppointments = appointmentRepository.countByCustomerId(customer.getId());
//
//        List<Status> activeStatuses = Arrays.asList(Status.NEW, Status.ONGOING);
//        Integer activeAppointments = appointmentRepository.countByCustomerIdAndStatusIn(
//                customer.getId(), activeStatuses);
//
//        Integer completedAppointments = appointmentRepository.countByCustomerIdAndStatus(
//                customer.getId(), Status.COMPLETED);
//
//        return new CustomerDashboardStatsResponse(
//                totalVehicles,
//                totalAppointments,
//                activeAppointments,
//                completedAppointments
//        );
//    }
//
//    /**
//     * Get all appointments for a customer with summary information
//     */
//    public List<AppointmentSummaryResponse> getCustomerAppointments(String username) {
//        Customer customer = customerRepository.findByUsername(username)
//                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with username: " + username));
//
//        List<Appointment> appointments = appointmentRepository.findByCustomerId(customer.getId());
//
//        return appointments.stream()
//                .map(this::mapToAppointmentSummary)
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Get active appointments only (NEW or ONGOING)
//     */
//    public List<AppointmentSummaryResponse> getActiveAppointments(String username) {
//        Customer customer = customerRepository.findByUsername(username)
//                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with username: " + username));
//
//        List<Status> activeStatuses = Arrays.asList(Status.NEW, Status.ONGOING);
//        List<Appointment> appointments = appointmentRepository.findActiveAppointmentsByCustomerId(
//                customer.getId(), activeStatuses);
//
//        return appointments.stream()
//                .map(this::mapToAppointmentSummary)
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Get detailed progress information for a specific appointment
//     */
//    public AppointmentProgressResponse getAppointmentProgress(String username, Long appointmentId) {
//        Customer customer = customerRepository.findByUsername(username)
//                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with username: " + username));
//
//        Appointment appointment = appointmentRepository.findByAppointmentIdAndCustomerId(
//                appointmentId, customer.getId())
//                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));
//
//        return mapToAppointmentProgress(appointment);
//    }
//
//    /**
//     * Map Appointment to AppointmentSummaryResponse
//     */
//    private AppointmentSummaryResponse mapToAppointmentSummary(Appointment appointment) {
//        Integer totalJobs = appointmentJobRepository.countByAppointmentId(appointment.getAppointmentId());
//        Integer completedJobs = appointmentJobRepository.countByAppointmentIdAndJobStatus(
//                appointment.getAppointmentId(), Status.COMPLETED);
//
//        Vehicle vehicle = appointment.getVehicle();
//        AppointmentSummaryResponse.VehicleBasicInfo vehicleInfo =
//                new AppointmentSummaryResponse.VehicleBasicInfo(
//                        vehicle.getVehicleId(),
//                        vehicle.getVehicleName(),
//                        vehicle.getRegistrationNo()
//                );
//
//        return new AppointmentSummaryResponse(
//                appointment.getAppointmentId(),
//                appointment.getAppointmentDate().format(DATE_FORMATTER),
//                appointment.getStartTime().format(TIME_FORMATTER),
//                appointment.getEndTime().format(TIME_FORMATTER),
//                appointment.getStatus(),
//                appointment.getTotalCost(),
//                vehicleInfo,
//                totalJobs,
//                completedJobs
//        );
//    }
//
//    /**
//     * Map Appointment to detailed AppointmentProgressResponse
//     */
//    private AppointmentProgressResponse mapToAppointmentProgress(Appointment appointment) {
//        AppointmentProgressResponse response = new AppointmentProgressResponse();
//
//        response.setAppointmentId(appointment.getAppointmentId());
//        response.setAppointmentDate(appointment.getAppointmentDate().format(DATE_FORMATTER));
//        response.setStartTime(appointment.getStartTime().format(TIME_FORMATTER));
//        response.setEndTime(appointment.getEndTime().format(TIME_FORMATTER));
//        response.setStatus(appointment.getStatus());
//        response.setTotalCost(appointment.getTotalCost());
//
//        // Map vehicle information
//        Vehicle vehicle = appointment.getVehicle();
//        AppointmentProgressResponse.VehicleInfo vehicleInfo = new AppointmentProgressResponse.VehicleInfo();
//        vehicleInfo.setVehicleId(vehicle.getVehicleId());
//        vehicleInfo.setVehicleName(vehicle.getVehicleName());
//        vehicleInfo.setRegistrationNo(vehicle.getRegistrationNo());
//        vehicleInfo.setVehicleType(vehicle.getVehicleType().name());
//        vehicleInfo.setModel(vehicle.getModel());
//        response.setVehicle(vehicleInfo);
//
//        // Map job information
//        List<AppointmentJob> jobs = appointmentJobRepository.findByAppointmentId(appointment.getAppointmentId());
//        List<AppointmentProgressResponse.JobProgressInfo> jobInfos = jobs.stream()
//                .map(this::mapToJobProgressInfo)
//                .collect(Collectors.toList());
//        response.setJobs(jobInfos);
//
//        return response;
//    }
//
//    /**
//     * Map AppointmentJob to JobProgressInfo
//     */
//    private AppointmentProgressResponse.JobProgressInfo mapToJobProgressInfo(AppointmentJob job) {
//        AppointmentProgressResponse.JobProgressInfo jobInfo = new AppointmentProgressResponse.JobProgressInfo();
//
//        jobInfo.setAppointmentJobId(job.getAppointmentJobId());
//        jobInfo.setServiceItemName(job.getServiceItem().getServiceItemName());
//        jobInfo.setServiceItemType(job.getServiceItem().getServiceItemType().name());
//        jobInfo.setJobStatus(job.getJobStatus());
//        jobInfo.setStartTime(job.getStartTime() != null ? job.getStartTime().format(TIME_FORMATTER) : null);
//        jobInfo.setEndTime(job.getEndTime() != null ? job.getEndTime().format(TIME_FORMATTER) : null);
//        jobInfo.setAdditionalCost(job.getAdditional_cost());
//        jobInfo.setJobNote(job.getJobNote());
//
//        // Count assigned employees
//        Integer assignedEmployees = job.getEmployeeAssignments() != null ?
//                job.getEmployeeAssignments().size() : 0;
//        jobInfo.setAssignedEmployees(assignedEmployees);
//
//        return jobInfo;
//    }
//}


