package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.*;
import com.EAD.autoservice_backend.model.*;
import com.EAD.autoservice_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final VehicleRepository vehicleRepository;
    private final ServiceItemRepository serviceItemRepository;
//    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final AppointmentRepository appointmentRepository;

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
        List<ServiceItem> selectedItems = serviceItemRepository
                .findAllById(request.getSelectedServiceItemIds());
        BigDecimal totalCost = selectedItems.stream()
                .map(ServiceItem::getServiceItemCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int totalDuration = selectedItems.stream()
                .mapToInt(ServiceItem::getEstimatedDuration)
                .sum();
        LocalTime estimatedEndTime = request.getStartTime().plusMinutes(totalDuration);

        return AppointmentCalculationResponse.builder()
                .totalCost(totalCost)
                .estimatedEndTime(estimatedEndTime)
                .build();
    }

    public AppointmentResponse createAppointmentTemp(AppointmentCreateRequest request, Long userId) {
        Customer customer=customerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Vehicle vehicle=vehicleRepository.findByVehicleId(request.getVehicleId());
        List<ServiceItem> serviceItems = serviceItemRepository.findAllById(request.getSelectedServiceItemIds());

        BigDecimal totalCost = serviceItems.stream()
                .map(ServiceItem::getServiceItemCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalDuration = serviceItems.stream()
                .mapToInt(ServiceItem::getEstimatedDuration)
                .sum();

        LocalTime estimatedEndTime = request.getStartTime().plusMinutes(totalDuration);

        Appointment appointment = Appointment.builder()
                .customer(customer)
                .vehicle(vehicle)
                .vehicleName(vehicle.getVehicleName())
                .appointmentDate(request.getAppointmentDate())
                .startTime(request.getStartTime())
                .endTime(estimatedEndTime)
                .totalCost(totalCost)
                .status(AppointmentStatus.NEW)
                .build();

        appointment=appointmentRepository.save(appointment);

        return AppointmentResponse.builder()
                .appointmentId(appointment.getAppointmentId())
                .vehicleName(appointment.getVehicleName())
                .appointmentDate(appointment.getAppointmentDate())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .totalCost(appointment.getTotalCost())
                .status(appointment.getStatus().name()) // assuming AppointmentStatus is an enum
                .selectedItems(
                        serviceItems.stream()
                                .map(item -> ServiceItemDTO.builder()
                                        .id(item.getServiceItemId())
                                        .name(item.getServiceItemName())
                                        .type(item.getServiceItemType().name())
                                        .build())
                                .toList()
                )
                .build();






    }
}
