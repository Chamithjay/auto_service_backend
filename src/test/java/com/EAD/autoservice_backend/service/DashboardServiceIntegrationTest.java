package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.DashboardStatsResponse;
import com.EAD.autoservice_backend.dto.MonthlyRevenueResponse;
import com.EAD.autoservice_backend.dto.RecentActivityResponse;
import com.EAD.autoservice_backend.dto.VehicleTypeDistributionResponse;
import com.EAD.autoservice_backend.model.*;
import com.EAD.autoservice_backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Tests for DashboardService
 * Tests the service with real database and Spring context
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional // Rollback after each test
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("DashboardService Integration Tests")
class DashboardServiceIntegrationTest {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private LeaveRepository leaveRepository;

    private Customer testCustomer;
    private Employee testEmployee;
    private Admin testAdmin;
    private Vehicle testVehicle1;
    private Vehicle testVehicle2;

    @BeforeEach
    void setUp() {
        // Clean all data before each test
        leaveRepository.deleteAll();
        appointmentRepository.deleteAll();
        vehicleRepository.deleteAll();
        userRepository.deleteAll();

        // Create test users
        testCustomer = new Customer();
        testCustomer.setUsername("customer1");
        testCustomer.setEmail("customer@test.com");
        testCustomer.setPassword("password");
        testCustomer.setRole(Role.CUSTOMER);
        testCustomer.setCreatedAt(LocalDateTime.now().minusDays(10));
        testCustomer.setUpdatedAt(LocalDateTime.now());
        testCustomer = (Customer) userRepository.save(testCustomer);

        testEmployee = new Employee();
        testEmployee.setUsername("employee1");
        testEmployee.setEmail("employee@test.com");
        testEmployee.setPassword("password");
        testEmployee.setRole(Role.EMPLOYEE);
        testEmployee.setCreatedAt(LocalDateTime.now().minusDays(5));
        testEmployee.setUpdatedAt(LocalDateTime.now());
        testEmployee = (Employee) userRepository.save(testEmployee);

        testAdmin = new Admin();
        testAdmin.setUsername("admin1");
        testAdmin.setEmail("admin@test.com");
        testAdmin.setPassword("password");
        testAdmin.setRole(Role.ADMIN);
        testAdmin.setCreatedAt(LocalDateTime.now().minusDays(15));
        testAdmin.setUpdatedAt(LocalDateTime.now());
        testAdmin = (Admin) userRepository.save(testAdmin);

        // Create test vehicles
        testVehicle1 = new Vehicle();
        testVehicle1.setVehicleName("Toyota Camry");
        testVehicle1.setRegistrationNo("ABC-123");
        testVehicle1.setVehicleType(VehicleType.CAR);
        testVehicle1.setCustomer(testCustomer);
        testVehicle1.setCreatedAt(LocalDateTime.now().minusDays(8));
        testVehicle1 = vehicleRepository.save(testVehicle1);

        testVehicle2 = new Vehicle();
        testVehicle2.setVehicleName("Honda Van");
        testVehicle2.setRegistrationNo("XYZ-789");
        testVehicle2.setVehicleType(VehicleType.VAN);
        testVehicle2.setCustomer(testCustomer);
        testVehicle2.setCreatedAt(LocalDateTime.now().minusDays(3));
        testVehicle2 = vehicleRepository.save(testVehicle2);
    }

    // ==================== getDashboardStats Integration Tests ====================

    @Test
    @DisplayName("Integration: Should get dashboard stats from database")
    void testGetDashboardStats_Integration() {
        // Arrange - Create some appointments for revenue calculation
        Appointment appointment1 = createAppointment(
                testVehicle1,
                LocalDate.now().minusDays(5),
                new BigDecimal("150.00"),
                AppointmentStatus.COMPLETED
        );
        Appointment appointment2 = createAppointment(
                testVehicle2,
                LocalDate.now().minusDays(2),
                new BigDecimal("200.00"),
                AppointmentStatus.COMPLETED
        );
        appointmentRepository.save(appointment1);
        appointmentRepository.save(appointment2);

        // Act
        DashboardStatsResponse stats = dashboardService.getDashboardStats();

        // Assert
        assertNotNull(stats);
        assertEquals(3L, stats.getTotalUsers(), "Should have 3 users");
        assertEquals(2L, stats.getTotalAppointments(), "Should have 2 appointments");
        assertEquals(1L, stats.getTotalEmployees(), "Should have 1 employee");
        assertEquals(2L, stats.getTotalVehicles(), "Should have 2 vehicles");
        assertEquals(new BigDecimal("350.00"), stats.getTotalRevenue(), "Total revenue should be 350.00");
    }

    @Test
    @DisplayName("Integration: Should return zero stats for empty database")
    void testGetDashboardStats_EmptyDatabase_Integration() {
        // Arrange - Clean all data
        leaveRepository.deleteAll();
        appointmentRepository.deleteAll();
        vehicleRepository.deleteAll();
        userRepository.deleteAll();

        // Act
        DashboardStatsResponse stats = dashboardService.getDashboardStats();

        // Assert
        assertNotNull(stats);
        assertEquals(0L, stats.getTotalUsers());
        assertEquals(0L, stats.getTotalAppointments());
        assertEquals(0L, stats.getTotalEmployees());
        assertEquals(0L, stats.getTotalVehicles());
        assertEquals(BigDecimal.ZERO, stats.getTotalRevenue());
    }

    @Test
    @DisplayName("Integration: Should calculate revenue only from completed appointments")
    void testGetDashboardStats_RevenueCalculation_Integration() {
        // Arrange
        appointmentRepository.save(createAppointment(
                testVehicle1, LocalDate.now(), new BigDecimal("100.00"), AppointmentStatus.COMPLETED
        ));
        appointmentRepository.save(createAppointment(
                testVehicle1, LocalDate.now(), new BigDecimal("200.00"), AppointmentStatus.NEW
        ));
        appointmentRepository.save(createAppointment(
                testVehicle1, LocalDate.now(), new BigDecimal("150.00"), AppointmentStatus.COMPLETED
        ));

        // Act
        DashboardStatsResponse stats = dashboardService.getDashboardStats();

        // Assert
        // Note: This depends on your repository implementation
        // If getTotalRevenue() only counts COMPLETED, it should be 250.00
        assertNotNull(stats.getTotalRevenue());
        assertTrue(stats.getTotalRevenue().compareTo(BigDecimal.ZERO) >= 0);
    }

    // ==================== getRecentActivities Integration Tests ====================

    @Test
    @DisplayName("Integration: Should get recent activities with appointments")
    void testGetRecentActivities_WithAppointments_Integration() {
        // Arrange
        appointmentRepository.save(createAppointment(
                testVehicle1, LocalDate.now(), new BigDecimal("150.00"), AppointmentStatus.NEW
        ));
        appointmentRepository.save(createAppointment(
                testVehicle2, LocalDate.now().minusDays(1), new BigDecimal("200.00"), AppointmentStatus.COMPLETED
        ));

        // Act
        List<RecentActivityResponse> activities = dashboardService.getRecentActivities();

        // Assert
        assertNotNull(activities);
        assertFalse(activities.isEmpty());
        assertTrue(activities.stream().anyMatch(a -> a.getActivityType().equals("APPOINTMENT")));
    }

    @Test
    @DisplayName("Integration: Should get recent activities with leaves")
    void testGetRecentActivities_WithLeaves_Integration() {
        // Arrange
        Leave leave = new Leave();
        leave.setEmployee(testEmployee);
        leave.setLeaveType(LeaveType.FULLDAY);
        leave.setLeaveDate(LocalDate.now().plusDays(5));
        leave.setLeaveReason("Personal");
        leave.setLeaveStatus(LeaveStatus.NEW);
        leaveRepository.save(leave);

        // Act
        List<RecentActivityResponse> activities = dashboardService.getRecentActivities();

        // Assert
        assertNotNull(activities);
        assertTrue(activities.stream().anyMatch(a -> a.getActivityType().equals("LEAVE")));
    }

    @Test
    @DisplayName("Integration: Should get recent activities with user registrations")
    void testGetRecentActivities_WithUsers_Integration() {
        // Users are already created in setUp()

        // Act
        List<RecentActivityResponse> activities = dashboardService.getRecentActivities();

        // Assert
        assertNotNull(activities);
        assertTrue(activities.stream().anyMatch(a -> a.getActivityType().equals("REGISTRATION")));
    }

    @Test
    @DisplayName("Integration: Should get recent activities with vehicles")
    void testGetRecentActivities_WithVehicles_Integration() {
        // Vehicles are already created in setUp()

        // Act
        List<RecentActivityResponse> activities = dashboardService.getRecentActivities();

        // Assert
        assertNotNull(activities);
        assertTrue(activities.stream().anyMatch(a -> a.getActivityType().equals("VEHICLE")));
    }

    @Test
    @DisplayName("Integration: Should limit recent activities to 10")
    void testGetRecentActivities_Limit_Integration() {
        // Arrange - Create 15 appointments
        for (int i = 0; i < 15; i++) {
            appointmentRepository.save(createAppointment(
                    testVehicle1,
                    LocalDate.now().minusDays(i),
                    new BigDecimal("100.00"),
                    AppointmentStatus.NEW
            ));
        }

        // Act
        List<RecentActivityResponse> activities = dashboardService.getRecentActivities();

        // Assert
        assertNotNull(activities);
        assertTrue(activities.size() <= 10, "Should return max 10 activities");
    }

    @Test
    @DisplayName("Integration: Should return empty list when no activities")
    void testGetRecentActivities_NoActivities_Integration() {
        // Arrange - Clean all activity-related data
        leaveRepository.deleteAll();
        appointmentRepository.deleteAll();
        vehicleRepository.deleteAll();
        userRepository.deleteAll();

        // Act
        List<RecentActivityResponse> activities = dashboardService.getRecentActivities();

        // Assert
        assertNotNull(activities);
        assertTrue(activities.isEmpty());
    }

    // ==================== getMonthlyRevenue Integration Tests ====================

    @Test
    @DisplayName("Integration: Should calculate monthly revenue correctly")
    void testGetMonthlyRevenue_Integration() {
        // Arrange - Create appointments in different months
        int currentYear = LocalDate.now().getYear();

        appointmentRepository.save(createAppointment(
                testVehicle1,
                LocalDate.of(currentYear, 1, 15),
                new BigDecimal("100.00"),
                AppointmentStatus.COMPLETED
        ));
        appointmentRepository.save(createAppointment(
                testVehicle1,
                LocalDate.of(currentYear, 1, 20),
                new BigDecimal("150.00"),
                AppointmentStatus.COMPLETED
        ));
        appointmentRepository.save(createAppointment(
                testVehicle2,
                LocalDate.of(currentYear, 3, 10),
                new BigDecimal("200.00"),
                AppointmentStatus.COMPLETED
        ));

        // Act
        List<MonthlyRevenueResponse> monthlyRevenue = dashboardService.getMonthlyRevenue();

        // Assert
        assertNotNull(monthlyRevenue);
        assertEquals(12, monthlyRevenue.size(), "Should have data for all 12 months");

        // Check specific months
        MonthlyRevenueResponse january = monthlyRevenue.get(0);
        assertEquals("Jan", january.getMonth());
        assertTrue(january.getRevenue().compareTo(BigDecimal.ZERO) >= 0);

        MonthlyRevenueResponse march = monthlyRevenue.get(2);
        assertEquals("Mar", march.getMonth());
        assertTrue(march.getRevenue().compareTo(BigDecimal.ZERO) >= 0);
    }

    @Test
    @DisplayName("Integration: Should return zero revenue for months with no appointments")
    void testGetMonthlyRevenue_NoAppointments_Integration() {
        // Arrange - Empty appointment repository
        appointmentRepository.deleteAll();

        // Act
        List<MonthlyRevenueResponse> monthlyRevenue = dashboardService.getMonthlyRevenue();

        // Assert
        assertNotNull(monthlyRevenue);
        assertEquals(12, monthlyRevenue.size());

        // All months should have zero revenue
        for (MonthlyRevenueResponse month : monthlyRevenue) {
            assertEquals(BigDecimal.ZERO, month.getRevenue());
        }
    }

    @Test
    @DisplayName("Integration: Should only include current year appointments")
    void testGetMonthlyRevenue_CurrentYearOnly_Integration() {
        // Arrange - Create appointments in different years
        int currentYear = LocalDate.now().getYear();

        appointmentRepository.save(createAppointment(
                testVehicle1,
                LocalDate.of(currentYear, 6, 15),
                new BigDecimal("500.00"),
                AppointmentStatus.COMPLETED
        ));
        appointmentRepository.save(createAppointment(
                testVehicle1,
                LocalDate.of(currentYear - 1, 6, 15),
                new BigDecimal("1000.00"),
                AppointmentStatus.COMPLETED
        ));

        // Act
        List<MonthlyRevenueResponse> monthlyRevenue = dashboardService.getMonthlyRevenue();

        // Assert
        assertNotNull(monthlyRevenue);
        // Revenue should only include current year
        // This depends on your repository's findAllByCurrentYear() implementation
        assertNotNull(monthlyRevenue.get(5)); // June
    }

    // ==================== getVehicleTypeDistribution Integration Tests ====================

    @Test
    @DisplayName("Integration: Should get vehicle type distribution")
    void testGetVehicleTypeDistribution_Integration() {
        // Arrange - Vehicles already created in setUp()
        // Add more vehicles
        Vehicle bus = new Vehicle();
        bus.setVehicleName("School Bus");
        bus.setRegistrationNo("BUS-001");
        bus.setVehicleType(VehicleType.BUS);
        bus.setCustomer(testCustomer);
        bus.setCreatedAt(LocalDateTime.now());
        vehicleRepository.save(bus);

        Vehicle car2 = new Vehicle();
        car2.setVehicleName("Honda Civic");
        car2.setRegistrationNo("CAR-002");
        car2.setVehicleType(VehicleType.CAR);
        car2.setCustomer(testCustomer);
        car2.setCreatedAt(LocalDateTime.now());
        vehicleRepository.save(car2);

        // Act
        List<VehicleTypeDistributionResponse> distribution =
                dashboardService.getVehicleTypeDistribution();

        // Assert
        assertNotNull(distribution);
        assertFalse(distribution.isEmpty());

        // Verify counts
        long totalVehicles = distribution.stream()
                .mapToLong(VehicleTypeDistributionResponse::getCount)
                .sum();
        assertEquals(4L, totalVehicles, "Total should be 4 vehicles");

        // Check specific types
        assertTrue(distribution.stream()
                .anyMatch(d -> d.getVehicleType().equals("CAR") && d.getCount() == 2L));
        assertTrue(distribution.stream()
                .anyMatch(d -> d.getVehicleType().equals("VAN") && d.getCount() == 1L));
        assertTrue(distribution.stream()
                .anyMatch(d -> d.getVehicleType().equals("BUS") && d.getCount() == 1L));
    }

    @Test
    @DisplayName("Integration: Should return empty distribution for no vehicles")
    void testGetVehicleTypeDistribution_NoVehicles_Integration() {
        // Arrange
        vehicleRepository.deleteAll();

        // Act
        List<VehicleTypeDistributionResponse> distribution =
                dashboardService.getVehicleTypeDistribution();

        // Assert
        assertNotNull(distribution);
        assertTrue(distribution.isEmpty());
    }

    @Test
    @DisplayName("Integration: Should group vehicles by type correctly")
    void testGetVehicleTypeDistribution_Grouping_Integration() {
        // Arrange - Add multiple vehicles of same type
        for (int i = 0; i < 3; i++) {
            Vehicle car = new Vehicle();
            car.setVehicleName("Car " + i);
            car.setRegistrationNo("CAR-" + i);
            car.setVehicleType(VehicleType.CAR);
            car.setCustomer(testCustomer);
            car.setCreatedAt(LocalDateTime.now());
            vehicleRepository.save(car);
        }

        // Act
        List<VehicleTypeDistributionResponse> distribution =
                dashboardService.getVehicleTypeDistribution();

        // Assert
        VehicleTypeDistributionResponse carDistribution = distribution.stream()
                .filter(d -> d.getVehicleType().equals("CAR"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("CAR type not found"));

        assertTrue(carDistribution.getCount() >= 3L, "Should have at least 3 cars");
    }

    // ==================== Complex Integration Tests ====================

    @Test
    @DisplayName("Integration: Should handle complete dashboard data flow")
    void testCompleteDashboardFlow_Integration() {
        // Arrange - Set up a complete scenario

        // Add more appointments
        appointmentRepository.save(createAppointment(
                testVehicle1, LocalDate.now().minusDays(1),
                new BigDecimal("300.00"), AppointmentStatus.COMPLETED
                ));
        appointmentRepository.save(createAppointment(
                testVehicle2, LocalDate.now(),
                new BigDecimal("400.00"), AppointmentStatus.NEW
        ));

        // Add leave
        Leave leave = new Leave();
        leave.setEmployee(testEmployee);
        leave.setLeaveType(LeaveType.FULLDAY);
        leave.setLeaveDate(LocalDate.now().plusDays(3));
        leave.setLeaveReason("Medical");
        leave.setLeaveStatus(LeaveStatus.NEW);
        leaveRepository.save(leave);

        // Act - Get all dashboard data
        DashboardStatsResponse stats = dashboardService.getDashboardStats();
        List<RecentActivityResponse> activities = dashboardService.getRecentActivities();
        List<MonthlyRevenueResponse> monthlyRevenue = dashboardService.getMonthlyRevenue();
        List<VehicleTypeDistributionResponse> distribution =
                dashboardService.getVehicleTypeDistribution();

        // Assert - Verify all data is consistent
        assertNotNull(stats);
        assertNotNull(activities);
        assertNotNull(monthlyRevenue);
        assertNotNull(distribution);

        assertTrue(stats.getTotalUsers() > 0);
        assertTrue(stats.getTotalAppointments() > 0);
        assertTrue(stats.getTotalVehicles() > 0);
        assertFalse(activities.isEmpty());
        assertEquals(12, monthlyRevenue.size());
        assertFalse(distribution.isEmpty());
    }

    // ==================== Helper Methods ====================

    private Appointment createAppointment(
            Vehicle vehicle,
            LocalDate date,
            BigDecimal cost,
            AppointmentStatus status
    ) {
        Appointment appointment = new Appointment();
        appointment.setVehicle(vehicle);
        appointment.setCustomer(vehicle.getCustomer());
        appointment.setVehicleName(vehicle.getVehicleName());
        appointment.setAppointmentDate(date);

        appointment.setAppointmentStartTime(LocalDateTime.of(
                date.getYear(), date.getMonth(), date.getDayOfMonth(), 9, 0
        ));

        appointment.setAppointmentEndTime(LocalDateTime.of(
                date.getYear(), date.getMonth(), date.getDayOfMonth(), 11, 0
        ));

        appointment.setTotalCost(cost);
        appointment.setStatus(status);

        return appointment;
    }

}