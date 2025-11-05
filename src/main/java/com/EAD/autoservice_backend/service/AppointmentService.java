package com.EAD.autoservice_backend.service;

import org.springframework.transaction.annotation.Transactional;
import com.EAD.autoservice_backend.dto.*;
import com.EAD.autoservice_backend.model.*;
import com.EAD.autoservice_backend.repository.*;
import com.EAD.autoservice_backend.exception.NoAvailableEmployeeException;
import com.EAD.autoservice_backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private JwtUtil jwtUtil;
    private final VehicleRepository vehicleRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final CustomerRepository customerRepository;
    private final AppointmentRepository appointmentRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveRepository leaveRepository;
    private final WorkSessionRepository workSessionRepository;
    private final AppointmentJobRepository appointmentJobRepository;
    private final JobAssignmentRepository jobAssignmentRepository;

    public UserInfoResponse getLoggedUserInfo(String token) {
        // Remove "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Extract user ID from JWT
        Long userId = jwtUtil.extractUserId(token);
        if (userId == null) {
            throw new RuntimeException("Invalid token: user ID not found");
        }

        // Retrieve the customer using the extracted user ID
        Customer customer = customerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Build and return response
        return UserInfoResponse.builder()
                .userId(customer.getId())
                .fullName(customer.getFullName())
                .email(customer.getEmail())
                .build();
    }

    public List<VehicleResponse> getVehiclesForUser(Long userId) {
        // Remove "Bearer " prefix if present
//        if (token.startsWith("Bearer ")) {
//            token = token.substring(7);
//        }
//
//        // Extract user ID from JWT
//        Long userId = jwtUtil.extractUserId(token);
//        if (userId == null) {
//            throw new RuntimeException("Invalid token: user ID not found");
//        }

        // Retrieve all vehicles for the user
        List<Vehicle> vehicles = vehicleRepository.findByCustomerId(userId);

        // Convert to response DTOs
        return vehicles.stream()
                .map(v -> new VehicleResponse(
                        v.getVehicleId(),
                        v.getVehicleName(),
                        v.getVehicleType()
                ))
                .toList();
    }
    private ServiceItemDTO mapToServiceItemDTO(ServiceItem item) {
        return ServiceItemDTO.builder()
                .id(item.getServiceItemId())
                .name(item.getServiceItemName())
                .type(item.getServiceItemType().name()) // Enum -> String
                .build();
    }


    public ServiceAndModificationResponse getServicesAndModificationsForVehicle(VehicleSelectionRequest request) {
        Vehicle vehicle=vehicleRepository.findByVehicleId(request.getVehicleId());
        VehicleType vehicleType=vehicle.getVehicleType();
        List<ServiceItem> allItems=serviceItemRepository.findByVehicleType(vehicleType);

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

        // ✅ Check each service item individually
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
//        if (token.startsWith("Bearer ")) {
//            token = token.substring(7);
//        }
//
//        Long userId = jwtUtil.extractUserId(token);
//        if (userId == null) {
//            throw new RuntimeException("Invalid token: user ID not found");
//        }

        Customer customer = customerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Vehicle vehicle = vehicleRepository.findByVehicleId(request.getVehicleId());

        List<ServiceItem> serviceItems = serviceItemRepository.findAllById(request.getSelectedServiceItemIds());

        BigDecimal totalCost = serviceItems.stream()
                .map(ServiceItem::getServiceItemCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalDuration = serviceItems.stream()
                .mapToInt(ServiceItem::getEstimatedDuration)
                .sum();

        Appointment appointment = Appointment.builder()
                .customer(customer)
                .vehicle(vehicle)
                .vehicleName(vehicle.getVehicleName())
                .appointmentDate(request.getAppointmentDate())
                .sessionType(request.getSessionType())
                .totalCost(totalCost)
                .totalApproximatedDuration(totalDuration)
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

            // ⭐ Use modified getNextEmployee method
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
                .totalApproximatedDuration(totalDuration)
                .status(appointment.getStatus().name())
                .message("Appointment created successfully.")
                .build();
    }



//    public List<AppointmentHistoryResponse> getCustomerAppointments(HttpServletRequest request) {
//        String authHeader = request.getHeader("Authorization");
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            throw new RuntimeException("Missing or invalid Authorization header");
//        }
//
//        String token = authHeader.substring(7);
//        Long customerId = jwtUtil.extractUserId(token);
//
//        // 1️⃣ Fetch all appointments for the logged-in customer
//        List<Appointment> appointments = appointmentRepository.findByCustomerId(customerId);
//
//        // 2️⃣ Build response list
//        return appointments.stream().map(appointment -> {
//            // Fetch all AppointmentJobs related to this appointment
//            List<AppointmentJob> jobs = appointmentJobRepository.findByAppointment(appointment);
//
//            // For each job, get the service/modification name
//            List<String> serviceNames = jobs.stream()
//                    .map(job -> job.getServiceItem().getServiceItemName())
//                    .toList();
//
//            return AppointmentHistoryResponse.builder()
//                    .appointmentId(appointment.getAppointmentId())
//                    .appointmentDate(appointment.getAppointmentDate())
//                    .sessionType(appointment.getSessionType())
//                    .status(appointment.getStatus())
//                    .totalCost(appointment.getTotalCost())
//                    .vehicleName(appointment.getVehicleName())
//                    .selectedServices(serviceNames)
//                    .build();
//        }).toList();
//    }

    public List<AppointmentHistoryResponse> getCustomerAppointments(Long customerId) {
        List<Appointment> appointments = appointmentRepository.findByCustomerId(customerId);

        return appointments.stream().map(appointment -> {
            List<AppointmentJob> jobs = appointmentJobRepository.findByAppointment(appointment);

            List<String> serviceNames = jobs.stream()
                    .map(job -> job.getServiceItem().getServiceItemName())
                    .toList();

            return AppointmentHistoryResponse.builder()
                    .appointmentId(appointment.getAppointmentId())
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
