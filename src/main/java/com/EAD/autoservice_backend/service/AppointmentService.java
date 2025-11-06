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

    private ServiceItemDTO mapToServiceItemDTO(ServiceItem item) {
        return ServiceItemDTO.builder()
                .id(item.getServiceItemId())
                .name(item.getServiceItemName())
                .type(item.getServiceItemType().name()) // Enum -> String
                .build();
    }


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

            // Total minutes already assigned to this employee in this session
            int totalMinutes = jobAssignmentRepository
                    .sumTotalDurationByDateAndEmployeeAndSession(candidate.getId(), date, sessionType);

            // Available minutes in session
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

        // Calculate total duration in minutes
        int totalDurationMinutes = serviceItems.stream()
                .mapToInt(ServiceItem::getEstimatedDuration)
                .sum();

        log.info("Total service duration: {} minutes for {} items", totalDurationMinutes, serviceItems.size());

        // Get work session to determine start time
        WorkSession workSession = workSessionRepository.findBySessionType(request.getSessionType())
                .orElseThrow(() -> new RuntimeException("Work session not configured for " + request.getSessionType()));

        // Calculate appointment start time: appointment date + work session start time
        LocalDateTime appointmentStartTime = LocalDateTime.of(
                request.getAppointmentDate(),
                workSession.getStartTime()
        );

        // Calculate appointment end time: start time + total duration
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
