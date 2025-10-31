package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.*;
import com.EAD.autoservice_backend.model.*;
import com.EAD.autoservice_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

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

    public UserInfoResponse getLoggedUserInfoTemp(Long userId) {
        Customer customer=customerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return UserInfoResponse.builder()
                .userId(customer.getId())
                .fullName(customer.getFullName())
                .email(customer.getEmail())
                .build();
    }

    public List<VehicleResponse> getVehiclesForUserTemp(Long userId) {
        List<Vehicle> vehicles=vehicleRepository.findByCustomerId(userId);
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
        // 1️⃣ Get selected service items
        List<ServiceItem> selectedItems = serviceItemRepository.findAllById(request.getSelectedServiceItemIds());
        if (selectedItems.isEmpty()) {
            return AppointmentCalculationResponse.builder()
                    .message("No service items selected.")
                    .build();
        }

        // 2️⃣ Calculate total cost and total duration
        BigDecimal totalCost = selectedItems.stream()
                .map(ServiceItem::getServiceItemCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalDuration = selectedItems.stream()
                .mapToInt(ServiceItem::getEstimatedDuration)
                .sum(); // total in minutes

        // 3️⃣ Get total employees
        long totalEmployees = employeeRepository.count();

        // 4️⃣ Get employees on leave for the selected day and session
        // 1️⃣ Extract the session from request
        SessionType sessionType = request.getSessionType();

// 2️⃣ Map to LeaveType
        LeaveType leaveType;
        switch (sessionType) {
            case MORNING -> leaveType = LeaveType.MORNING;
            case EVENING -> leaveType = LeaveType.EVENING;
            default -> throw new IllegalArgumentException("Invalid session type");
        }

// 3️⃣ Call repository with correct LeaveType
        long employeesOnLeave = leaveRepository.countEmployeesOnLeave(
                request.getAppointmentDate(),
                leaveType
        );

        long availableEmployees = totalEmployees - employeesOnLeave;
        if (availableEmployees <= 0) {
            return AppointmentCalculationResponse.builder()
                    .message("No employees available for the selected date and session.")
                    .build();
        }

        // 5️⃣ Get work session hours (e.g., MORNING = 5 hours, EVENING = 4 hours)
        double sessionHours = workSessionRepository.findBySessionType(request.getSessionType())
                .map(WorkSession::getDurationHours)
                .orElse(5.0); // fallback

        // total man hours = available employees × session hours
        double totalManHours = availableEmployees * sessionHours;

        // 6️⃣ Get total duration already booked (in hours)
        long usedMinutes = appointmentRepository.sumTotalDurationByDateAndSession(
                request.getAppointmentDate(),
                request.getSessionType()
        );


        double usedHours = usedMinutes / 60.0;
        double requiredHours = totalDuration / 60.0;

        // 7️⃣ Check availability
        if ((usedHours + requiredHours) > totalManHours) {
            return AppointmentCalculationResponse.builder()
                    .message("No available slots for the selected date and session.")
                    .build();
        }

        // ✅ Success case
        return AppointmentCalculationResponse.builder()
                .totalCost(totalCost)
                .message("Slot available for the selected session.")
                .build();
    }
//    public AppointmentResponse createAppointmentTemp(AppointmentCreateRequest request, Long userId) {
//        Customer customer=customerRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("Customer not found"));
//        Vehicle vehicle=vehicleRepository.findByVehicleId(request.getVehicleId());
//        List<ServiceItem> serviceItems = serviceItemRepository.findAllById(request.getSelectedServiceItemIds());
//
//        BigDecimal totalCost = serviceItems.stream()
//                .map(ServiceItem::getServiceItemCost)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        int totalDuration = serviceItems.stream()
//                .mapToInt(ServiceItem::getEstimatedDuration)
//                .sum();
//
//        LocalTime estimatedEndTime = request.getStartTime().plusMinutes(totalDuration);
//
//        Appointment appointment = Appointment.builder()
//                .customer(customer)
//                .vehicle(vehicle)
//                .vehicleName(vehicle.getVehicleName())
//                .appointmentDate(request.getAppointmentDate())
//                .startTime(request.getStartTime())
//                .endTime(estimatedEndTime)
//                .totalCost(totalCost)
//                .status(AppointmentStatus.NEW)
//                .build();
//
//        appointment=appointmentRepository.save(appointment);
//
//        return AppointmentResponse.builder()
//                .appointmentId(appointment.getAppointmentId())
//                .vehicleName(appointment.getVehicleName())
//                .appointmentDate(appointment.getAppointmentDate())
//                .startTime(appointment.getStartTime())
//                .endTime(appointment.getEndTime())
//                .totalCost(appointment.getTotalCost())
//                .status(appointment.getStatus().name()) // assuming AppointmentStatus is an enum
//                .selectedItems(
//                        serviceItems.stream()
//                                .map(item -> ServiceItemDTO.builder()
//                                        .id(item.getServiceItemId())
//                                        .name(item.getServiceItemName())
//                                        .type(item.getServiceItemType().name())
//                                        .build())
//                                .toList()
//                )
//                .build();
//    }
}
