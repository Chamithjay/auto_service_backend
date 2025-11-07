package com.EAD.autoservice_backend.service;

import org.springframework.transaction.annotation.Transactional;
import com.EAD.autoservice_backend.dto.*;
import com.EAD.autoservice_backend.model.*;
import com.EAD.autoservice_backend.repository.*;
import com.EAD.autoservice_backend.exception.NoAvailableEmployeeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.time.LocalDate;

/**
 * Service class for managing appointments.
 * Handles appointment creation, calculation, employee assignment, and appointment history.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final VehicleRepository vehicleRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final CustomerRepository customerRepository;
    private final AppointmentRepository appointmentRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveRepository leaveRepository;
    private final WorkSessionRepository workSessionRepository;
    private final AppointmentJobRepository appointmentJobRepository;
    private final JobAssignmentRepository jobAssignmentRepository;

    /**
     * Retrieves all vehicles for a specific user.
     *
     * @param userId the user ID
     * @return list of vehicle responses
     */
    public List<VehicleResponse> getVehiclesForUser(Long userId) {
        log.info("Fetching vehicles for user ID: {}", userId);
        List<Vehicle> vehicles = vehicleRepository.findByCustomerId(userId);
        log.info("Found {} vehicles for user ID: {}", vehicles.size(), userId);

        List<VehicleResponse> response = vehicles.stream()
                .map(v -> {
                    log.debug("Mapping vehicle: ID={}, Name={}, Type={}",
                            v.getVehicleId(), v.getVehicleName(), v.getVehicleType());
                    return new VehicleResponse(
                            v.getVehicleId(),
                            v.getVehicleName(),
                            v.getVehicleType()
                    );
                })
                .toList();

        log.info("Returning {} vehicle responses", response.size());
        return response;
    }

    /**
     * Maps a ServiceItem entity to a ServiceItemDTO.
     *
     * @param item the service item entity
     * @return the service item DTO
     */
    private ServiceItemDTO mapToServiceItemDTO(ServiceItem item) {
        return ServiceItemDTO.builder()
                .id(item.getServiceItemId())
                .name(item.getServiceItemName())
                .type(item.getServiceItemType().name())
                .build();
    }

    /**
     * Retrieves available services and modifications for a specific vehicle.
     *
     * @param request the vehicle selection request
     * @return response containing services and modifications
     * @throws RuntimeException if vehicle is not found
     */
    public ServiceAndModificationResponse getServicesAndModificationsForVehicle(VehicleSelectionRequest request) {
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        VehicleType vehicleType = vehicle.getVehicleType();
        List<ServiceItem> allItems = serviceItemRepository.findByVehicleType(vehicleType);

        List<ServiceItemDTO> services = allItems.stream()
                .filter(i -> i.getServiceItemType() == ServiceItemType.SERVICE)
                .map(this::mapToServiceItemDTO)
                .toList();

        List<ServiceItemDTO> modifications = allItems.stream()
                .filter(i -> i.getServiceItemType() == ServiceItemType.MODIFICATION)
                .map(this::mapToServiceItemDTO)
                .toList();

        return ServiceAndModificationResponse.builder()
                .services(services)
                .modifications(modifications)
                .build();
    }

    /**
     * Calculates appointment details including total cost and employee availability.
     * Validates if there are enough available employees for the selected services.
     *
     * @param request the appointment calculation request
     * @return calculation response with cost and availability message
     */
    public AppointmentCalculationResponse calculateAppointmentDetails(AppointmentCalculationRequest request) {

        List<ServiceItem> selectedItems = serviceItemRepository.findAllById(request.getSelectedServiceItemIds());
        if (selectedItems.isEmpty()) {
            return AppointmentCalculationResponse.builder()
                    .message("No service items selected.")
                    .build();
        }

        BigDecimal totalCost = selectedItems.stream()
                .map(ServiceItem::getServiceItemCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        SessionType sessionType = request.getSessionType();
        LeaveType leaveType = (sessionType == SessionType.MORNING) ? LeaveType.HALFDAY_MORNING : LeaveType.HALFDAY_EVENING;

        List<Employee> allEmployees = employeeRepository.findAll();
        if (allEmployees.isEmpty()) {
            return AppointmentCalculationResponse.builder()
                    .message("No employees found in the system.")
                    .build();
        }

        List<Employee> availableEmployees = allEmployees.stream()
                .filter(emp -> !leaveRepository.isEmployeeOnApprovedLeave(emp.getId(), request.getAppointmentDate(), leaveType))
                .toList();

        if (availableEmployees.isEmpty()) {
            return AppointmentCalculationResponse.builder()
                    .message("No employees available for the selected date and session.")
                    .build();
        }

        int sessionMinutes = (int) (workSessionRepository.findBySessionType(sessionType)
                .map(WorkSession::getDurationHours)
                .orElse(5.0) * 60);

        for (ServiceItem item : selectedItems) {
            int serviceDuration = item.getEstimatedDuration();

            boolean canBeAssigned = availableEmployees.stream()
                    .anyMatch(emp -> {
                        int usedMinutes = jobAssignmentRepository
                                .sumTotalDurationByDateAndEmployeeAndSession(emp.getId(), request.getAppointmentDate(), sessionType);
                        return (usedMinutes + serviceDuration) <= sessionMinutes;
                    });

            if (!canBeAssigned) {
                return AppointmentCalculationResponse.builder()
                        .message("No available employees can take the service item: " + item.getServiceItemName())
                        .build();
            }
        }

        return AppointmentCalculationResponse.builder()
                .totalCost(totalCost)
                .message("Slot available for the selected session.")
                .build();
    }

    /**
     * Finds the next available employee for a job assignment using round-robin selection.
     * Ensures fair distribution of work among available employees.
     *
     * @param lastEmployeeId the ID of the last assigned employee
     * @param date the appointment date
     * @param newJobDuration the duration of the new job in minutes
     * @param sessionType the session type (MORNING or EVENING)
     * @param availableEmployees list of available employees
     * @return the next available employee
     * @throws NoAvailableEmployeeException if no employee is available
     * @throws RuntimeException if work session is not configured
     */
    private Employee getNextEmployee(
            Long lastEmployeeId,
            LocalDate date,
            int newJobDuration,
            SessionType sessionType,
            List<Employee> availableEmployees
    ) {
        int lastIndex = -1;

        for (int i = 0; i < availableEmployees.size(); i++) {
            if (availableEmployees.get(i).getId().equals(lastEmployeeId)) {
                lastIndex = i;
                break;
            }
        }

        for (int offset = 1; offset <= availableEmployees.size(); offset++) {
            Employee candidate = availableEmployees.get((lastIndex + offset) % availableEmployees.size());

            int totalMinutes = jobAssignmentRepository
                    .sumTotalDurationByDateAndEmployeeAndSession(candidate.getId(), date, sessionType);

            WorkSession session = workSessionRepository
                    .findBySessionType(sessionType)
                    .orElseThrow(() -> new RuntimeException("Work session not configured"));

            int availableMinutes = (int) (session.getDurationHours() * 60);

            if (totalMinutes + newJobDuration <= availableMinutes) {
                return candidate;
            }
        }

        throw new NoAvailableEmployeeException("No available employee for the session.");
    }

    /**
     * Creates a new appointment with automatic employee assignment.
     * Calculates appointment times, creates appointment jobs, and assigns employees using round-robin.
     *
     * @param request the appointment creation request
     * @param userId the customer user ID
     * @return appointment response with details
     * @throws RuntimeException if customer, vehicle, or work session is not found
     * @throws NoAvailableEmployeeException if no employees are available
     */
    @Transactional
    public AppointmentResponse createAppointment(AppointmentCreateRequest request,@RequestParam Long userId ) {

        Customer customer = customerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        List<ServiceItem> serviceItems = serviceItemRepository.findAllById(request.getSelectedServiceItemIds());

        BigDecimal totalCost = serviceItems.stream()
                .map(ServiceItem::getServiceItemCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalDurationMinutes = serviceItems.stream()
                .mapToInt(ServiceItem::getEstimatedDuration)
                .sum();

        log.info("Total service duration: {} minutes for {} items", totalDurationMinutes, serviceItems.size());

        WorkSession workSession = workSessionRepository.findBySessionType(request.getSessionType())
                .orElseThrow(() -> new RuntimeException("Work session not configured for " + request.getSessionType()));

        LocalDateTime appointmentStartTime = LocalDateTime.of(
                request.getAppointmentDate(),
                workSession.getStartTime()
        );

        LocalDateTime appointmentEndTime = appointmentStartTime.plusMinutes(totalDurationMinutes);

        log.info("Appointment scheduled for {} from {} to {} ({} minutes)",
                request.getAppointmentDate(),
                appointmentStartTime.toLocalTime(),
                appointmentEndTime.toLocalTime(),
                totalDurationMinutes);

        Appointment appointment = Appointment.builder()
                .customer(customer)
                .vehicle(vehicle)
                .vehicleName(vehicle.getVehicleName())
                .appointmentDate(request.getAppointmentDate())
                .sessionType(request.getSessionType())
                .totalCost(totalCost)
                .appointmentStartTime(appointmentStartTime)
                .appointmentEndTime(appointmentEndTime)
                .status(AppointmentStatus.NEW)
                .build();

        appointmentRepository.save(appointment);

        // Determine leave type based on session
        LeaveType leaveType = (request.getSessionType() == SessionType.MORNING)
                ? LeaveType.HALFDAY_MORNING
                : LeaveType.HALFDAY_EVENING;

        // Filter only employees not on approved leave
        List<Employee> availableEmployees = employeeRepository.findAllByOrderByIdAsc().stream()
                .filter(emp -> !leaveRepository.isEmployeeOnApprovedLeave(
                        emp.getId(),
                        request.getAppointmentDate(),
                        leaveType
                ))
                .toList();

        if (availableEmployees.isEmpty()) {
            throw new NoAvailableEmployeeException("No employees available due to approved leave");
        }

        Long lastEmployeeId = jobAssignmentRepository.findLastAssignedEmployeeId().orElse(null);

        for (ServiceItem item : serviceItems) {
            int newJobDuration = item.getEstimatedDuration();

            Employee nextEmployee = getNextEmployee(
                    lastEmployeeId,
                    request.getAppointmentDate(),
                    newJobDuration,
                    request.getSessionType(),
                    availableEmployees
            );

            AppointmentJob job = new AppointmentJob();
            job.setAppointment(appointment);
            job.setServiceItem(item);
            job.setItemStatus(AppointmentStatus.NEW);
            job.setEmployeeAssignments(new HashSet<>());
            appointmentJobRepository.save(job);

            JobAssignment assignment = new JobAssignment();
            assignment.setAppointmentJob(job);
            assignment.setEmployee(nextEmployee);
            jobAssignmentRepository.save(assignment);

            job.getEmployeeAssignments().add(assignment);
            appointmentJobRepository.save(job);

            lastEmployeeId = nextEmployee.getId();
        }

        return AppointmentResponse.builder()
                .appointmentId(appointment.getAppointmentId())
                .vehicleName(appointment.getVehicleName())
                .appointmentDate(appointment.getAppointmentDate())
                .sessionType(appointment.getSessionType())
                .totalCost(appointment.getTotalCost())
                .status(appointment.getStatus().name())
                .message("Appointment created successfully.")
                .build();
    }

    /**
     * Retrieves appointment history for a customer.
     * If date range is not provided, returns the 15 most recent appointments.
     *
     * @param customerId the customer ID
     * @param startDate the start date for filtering (optional)
     * @param endDate the end date for filtering (optional)
     * @return list of appointment history responses
     */
    public List<AppointmentHistoryResponse> getCustomerAppointments(Long customerId, LocalDate startDate, LocalDate endDate) {
        List<Appointment> appointments;

        if (startDate == null || endDate == null) {
            appointments = appointmentRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                    .stream()
                    .limit(15)
                    .toList();
        } else {
            LocalDateTime from = startDate.atStartOfDay();
            LocalDateTime to = endDate.atTime(23, 59, 59);

            appointments = appointmentRepository.findByCustomerIdAndDateRange(customerId, from, to);
        }

        return appointments.stream().map(appointment -> {
            List<AppointmentJob> jobs = appointmentJobRepository.findByAppointment(appointment);

            List<String> serviceNames = jobs.stream()
                    .map(job -> job.getServiceItem().getServiceItemName())
                    .toList();

            return AppointmentHistoryResponse.builder()
                    .appointmentId(appointment.getAppointmentId())
                    .createdAt(appointment.getCreatedAt())
                    .appointmentDate(appointment.getAppointmentDate())
                    .sessionType(appointment.getSessionType())
                    .status(appointment.getStatus())
                    .totalCost(appointment.getTotalCost())
                    .vehicleName(appointment.getVehicleName())
                    .selectedServices(serviceNames)
                    .build();
        }).toList();
    }
}
