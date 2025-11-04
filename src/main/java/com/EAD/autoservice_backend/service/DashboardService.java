package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.DashboardStatsResponse;
import com.EAD.autoservice_backend.dto.MonthlyRevenueResponse;
import com.EAD.autoservice_backend.dto.RecentActivityResponse;
import com.EAD.autoservice_backend.dto.VehicleTypeDistributionResponse;
import com.EAD.autoservice_backend.model.*;
import com.EAD.autoservice_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final VehicleRepository vehicleRepository;
    private final LeaveRepository leaveRepository;

    @Autowired
    public DashboardService(UserRepository userRepository,
                            AppointmentRepository appointmentRepository,
                            VehicleRepository vehicleRepository,
                            LeaveRepository leaveRepository) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.vehicleRepository = vehicleRepository;
        this.leaveRepository = leaveRepository;
    }

    /**
     * Get main dashboard statistics
     */
    public DashboardStatsResponse getDashboardStats() {
        Long totalUsers = userRepository.count();
        Long totalAppointments = appointmentRepository.count();

        // Count employees by role
        Long totalEmployees = userRepository.countByRole(Role.EMPLOYEE);

        Long totalVehicles = vehicleRepository.count();
        BigDecimal totalRevenue = appointmentRepository.getTotalRevenue();

        return new DashboardStatsResponse(
                totalUsers,
                totalAppointments,
                totalEmployees,
                totalVehicles,
                totalRevenue != null ? totalRevenue : BigDecimal.ZERO
        );
    }

    /**
     * Get recent activities (last 10)
     */
    public List<RecentActivityResponse> getRecentActivities() {
        List<RecentActivityResponse> activities = new ArrayList<>();

        // Get recent appointments (new ones)
        List<Appointment> recentAppointments = appointmentRepository.findAllOrderByDateDesc();
        recentAppointments.stream()
                .limit(3)
                .forEach(appointment -> {
                    String description = appointment.getStatus() == Status.NEW
                            ? "New appointment created"
                            : appointment.getStatus() == Status.COMPLETED
                            ? "Appointment completed"
                            : "Appointment in progress";

                    activities.add(new RecentActivityResponse(
                            "APPOINTMENT",
                            description,
                            appointment.getVehicle().getCustomer().getUsername(),
                            appointment.getVehicle().getCreatedAt(),
                            appointment.getStatus().name()
                    ));
                });

        // Get pending leave requests
        List<Leave> pendingLeaves = leaveRepository.findByLeaveStatusOrderByLeaveDateDesc(LeaveStatus.NEW);
        pendingLeaves.stream()
                .limit(2)
                .forEach(leave -> {
                    activities.add(new RecentActivityResponse(
                            "LEAVE",
                            "Leave request pending approval",
                            leave.getEmployee().getUsername(),
                            LocalDateTime.now(),
                            "PENDING"
                    ));
                });

        // Get recently registered users
        List<User> recentUsers = userRepository.findAll().stream()
                .sorted(Comparator.comparing(User::getCreatedAt).reversed())
                .limit(3)
                .collect(Collectors.toList());

        recentUsers.forEach(user -> {
            activities.add(new RecentActivityResponse(
                    "REGISTRATION",
                    "New user registered",
                    user.getUsername(),
                    user.getCreatedAt(),
                    user.getRole().name()
            ));
        });

        // Get recent vehicles
        List<Vehicle> recentVehicles = vehicleRepository.findAllByOrderByCreatedAtDesc();
        recentVehicles.stream()
                .limit(2)
                .forEach(vehicle -> {
                    activities.add(new RecentActivityResponse(
                            "VEHICLE",
                            "New vehicle registered: " + vehicle.getVehicleName() + " (" + vehicle.getRegistrationNo() + ")",
                            vehicle.getCustomer().getUsername(),
                            vehicle.getCreatedAt(),
                            "NEW"
                    ));
                });

        // Sort all activities by timestamp
        activities.sort(Comparator.comparing(RecentActivityResponse::getTimestamp).reversed());

        // Return only top 10
        return activities.stream().limit(10).collect(Collectors.toList());
    }

    /**
     * Get monthly revenue for current year
     */
    public List<MonthlyRevenueResponse> getMonthlyRevenue() {
        // Get all appointments for current year
        List<Appointment> appointments = appointmentRepository.findAllByCurrentYear();

        // Initialize all months with 0
        Map<Integer, BigDecimal> monthlyData = new LinkedHashMap<>();
        for (int i = 1; i <= 12; i++) {
            monthlyData.put(i, BigDecimal.ZERO);
        }

        // Aggregate revenue by month
        for (Appointment appointment : appointments) {
            int month = appointment.getAppointmentDate().getMonthValue();
            BigDecimal currentRevenue = monthlyData.get(month);
            BigDecimal appointmentCost = appointment.getTotalCost() != null ? appointment.getTotalCost() : BigDecimal.ZERO;
            monthlyData.put(month, currentRevenue.add(appointmentCost));
        }

        // Convert to response list with month names
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        List<MonthlyRevenueResponse> result = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            result.add(new MonthlyRevenueResponse(
                    monthNames[i - 1],
                    monthlyData.get(i)
            ));
        }

        return result;
    }

    /**
     * Get vehicle type distribution
     */
    public List<VehicleTypeDistributionResponse> getVehicleTypeDistribution() {
        List<Object[]> results = vehicleRepository.countByVehicleType();

        return results.stream()
                .map(result -> new VehicleTypeDistributionResponse(
                        ((VehicleType) result[0]).name(),
                        (Long) result[1]
                ))
                .collect(Collectors.toList());
    }
}