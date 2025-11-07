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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit Tests for DashboardService
 * Uses Mockito to mock dependencies
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DashboardService Unit Tests")
class DashboardServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private LeaveRepository leaveRepository;

    @InjectMocks
    private DashboardService dashboardService;

    private List<User> testUsers;
    private List<Appointment> testAppointments;
    private List<Vehicle> testVehicles;
    private List<Leave> testLeaves;
    private Customer testCustomer;
    private Employee testEmployee;
    private Vehicle testVehicle;

    @BeforeEach
    void setUp() {
        // Setup test data
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setUsername("customer1");
        testCustomer.setEmail("customer@example.com");
        testCustomer.setRole(Role.CUSTOMER);
        testCustomer.setCreatedAt(LocalDateTime.now().minusDays(5));

        testEmployee = new Employee();
        testEmployee.setId(2L);
        testEmployee.setUsername("employee1");
        testEmployee.setEmail("employee@example.com");
        testEmployee.setRole(Role.EMPLOYEE);
        testEmployee.setCreatedAt(LocalDateTime.now().minusDays(10));

        testUsers = Arrays.asList(testCustomer, testEmployee);

        // Setup vehicle
        testVehicle = new Vehicle();
        testVehicle.setVehicleId(1L);
        testVehicle.setVehicleName("Toyota Camry");
        testVehicle.setRegistrationNo("ABC-123");
        testVehicle.setVehicleType(VehicleType.CAR);
        testVehicle.setCustomer(testCustomer);
        testVehicle.setCreatedAt(LocalDateTime.now().minusDays(3));

        testVehicles = Arrays.asList(testVehicle);

        // Setup appointments
        Appointment appointment1 = new Appointment();
        appointment1.setAppointmentId(1L);
        appointment1.setAppointmentDate(LocalDate.now().minusDays(2));
        appointment1.setAppointmentStartTime(LocalDateTime.of(2024, 11, 10, 9, 0));
        appointment1.setAppointmentEndTime(LocalDateTime.of(2024, 11, 10, 10, 0));
        appointment1.setStatus(AppointmentStatus.COMPLETED);
        appointment1.setTotalCost(new BigDecimal("250.00"));
        appointment1.setVehicle(testVehicle);

        Appointment appointment2 = new Appointment();
        appointment2.setAppointmentId(2L);
        appointment2.setAppointmentDate(LocalDate.now());
        appointment2.setAppointmentStartTime(LocalDateTime.of(2024, 11, 10, 9, 0));
        appointment2.setAppointmentEndTime(LocalDateTime.of(2024, 11, 10, 10, 0));
        appointment2.setStatus(AppointmentStatus.NEW);
        appointment2.setTotalCost(new BigDecimal("180.00"));
        appointment2.setVehicle(testVehicle);

        testAppointments = Arrays.asList(appointment1, appointment2);

        // Setup leaves
        Leave leave1 = new Leave();
        leave1.setLeaveId(1L);
        leave1.setLeaveType(LeaveType.FULLDAY);
        leave1.setLeaveDate(LocalDate.now().plusDays(5));
        leave1.setLeaveReason("Personal");
        leave1.setLeaveStatus(LeaveStatus.NEW);
        leave1.setEmployee(testEmployee);

        testLeaves = Arrays.asList(leave1);
    }

    // ==================== getDashboardStats Tests ====================

    @Test
    @DisplayName("Should get dashboard stats successfully")
    void testGetDashboardStats_Success() {
        // Arrange
        when(userRepository.count()).thenReturn(10L);
        when(appointmentRepository.count()).thenReturn(25L);
        when(userRepository.countByRole(Role.EMPLOYEE)).thenReturn(5L);
        when(vehicleRepository.count()).thenReturn(15L);
        when(appointmentRepository.getTotalRevenue()).thenReturn(new BigDecimal("5000.00"));

        // Act
        DashboardStatsResponse response = dashboardService.getDashboardStats();

        // Assert
        assertNotNull(response);
        assertEquals(10L, response.getTotalUsers());
        assertEquals(25L, response.getTotalAppointments());
        assertEquals(5L, response.getTotalEmployees());
        assertEquals(15L, response.getTotalVehicles());
        assertEquals(new BigDecimal("5000.00"), response.getTotalRevenue());

        verify(userRepository, times(1)).count();
        verify(appointmentRepository, times(1)).count();
        verify(userRepository, times(1)).countByRole(Role.EMPLOYEE);
        verify(vehicleRepository, times(1)).count();
        verify(appointmentRepository, times(1)).getTotalRevenue();
    }

    @Test
    @DisplayName("Should return zero revenue when null")
    void testGetDashboardStats_NullRevenue() {
        // Arrange
        when(userRepository.count()).thenReturn(10L);
        when(appointmentRepository.count()).thenReturn(25L);
        when(userRepository.countByRole(Role.EMPLOYEE)).thenReturn(5L);
        when(vehicleRepository.count()).thenReturn(15L);
        when(appointmentRepository.getTotalRevenue()).thenReturn(null);

        // Act
        DashboardStatsResponse response = dashboardService.getDashboardStats();

        // Assert
        assertNotNull(response);
        assertEquals(BigDecimal.ZERO, response.getTotalRevenue());
    }

    // ==================== getRecentActivities Tests ====================

    @Test
    @DisplayName("Should get recent activities successfully")
    void testGetRecentActivities_Success() {
        // Arrange
        when(appointmentRepository.findAllOrderByDateDesc()).thenReturn(testAppointments);
        when(leaveRepository.findByLeaveStatusOrderByLeaveDateDesc(LeaveStatus.NEW)).thenReturn(testLeaves);
        when(userRepository.findAll()).thenReturn(testUsers);
        when(vehicleRepository.findAllByOrderByCreatedAtDesc()).thenReturn(testVehicles);

        // Act
        List<RecentActivityResponse> activities = dashboardService.getRecentActivities();

        // Assert
        assertNotNull(activities);
        assertFalse(activities.isEmpty());
        assertTrue(activities.size() <= 10); // Should return max 10 activities

        verify(appointmentRepository, times(1)).findAllOrderByDateDesc();
        verify(leaveRepository, times(1)).findByLeaveStatusOrderByLeaveDateDesc(LeaveStatus.NEW);
        verify(userRepository, times(1)).findAll();
        verify(vehicleRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("Should return empty list when no activities")
    void testGetRecentActivities_NoData() {
        // Arrange
        when(appointmentRepository.findAllOrderByDateDesc()).thenReturn(new ArrayList<>());
        when(leaveRepository.findByLeaveStatusOrderByLeaveDateDesc(LeaveStatus.NEW)).thenReturn(new ArrayList<>());
        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        when(vehicleRepository.findAllByOrderByCreatedAtDesc()).thenReturn(new ArrayList<>());

        // Act
        List<RecentActivityResponse> activities = dashboardService.getRecentActivities();

        // Assert
        assertNotNull(activities);
        assertTrue(activities.isEmpty());
    }

    // ==================== getMonthlyRevenue Tests ====================

    @Test
    @DisplayName("Should get monthly revenue successfully")
    void testGetMonthlyRevenue_Success() {
        // Arrange
        when(appointmentRepository.findAllByCurrentYear()).thenReturn(testAppointments);

        // Act
        List<MonthlyRevenueResponse> revenue = dashboardService.getMonthlyRevenue();

        // Assert
        assertNotNull(revenue);
        assertEquals(12, revenue.size()); // Should have all 12 months

        // Verify month names
        assertEquals("Jan", revenue.get(0).getMonth());
        assertEquals("Dec", revenue.get(11).getMonth());

        verify(appointmentRepository, times(1)).findAllByCurrentYear();
    }

    @Test
    @DisplayName("Should return zero revenue for all months when no appointments")
    void testGetMonthlyRevenue_NoAppointments() {
        // Arrange
        when(appointmentRepository.findAllByCurrentYear()).thenReturn(new ArrayList<>());

        // Act
        List<MonthlyRevenueResponse> revenue = dashboardService.getMonthlyRevenue();

        // Assert
        assertNotNull(revenue);
        assertEquals(12, revenue.size());

        // All months should have zero revenue
        for (MonthlyRevenueResponse month : revenue) {
            assertEquals(BigDecimal.ZERO, month.getRevenue());
        }
    }

    // ==================== getVehicleTypeDistribution Tests ====================

    @Test
    @DisplayName("Should get vehicle type distribution successfully")
    void testGetVehicleTypeDistribution_Success() {
        // Arrange
        List<Object[]> mockData = Arrays.asList(
                new Object[]{VehicleType.CAR, 10L},
                new Object[]{VehicleType.VAN, 5L},
                new Object[]{VehicleType.BUS, 2L}
        );
        when(vehicleRepository.countByVehicleType()).thenReturn(mockData);

        // Act
        List<VehicleTypeDistributionResponse> distribution = dashboardService.getVehicleTypeDistribution();

        // Assert
        assertNotNull(distribution);
        assertEquals(3, distribution.size());

        assertEquals("CAR", distribution.get(0).getVehicleType());
        assertEquals(10L, distribution.get(0).getCount());

        assertEquals("VAN", distribution.get(1).getVehicleType());
        assertEquals(5L, distribution.get(1).getCount());

        assertEquals("BUS", distribution.get(2).getVehicleType());
        assertEquals(2L, distribution.get(2).getCount());

        verify(vehicleRepository, times(1)).countByVehicleType();
    }

    @Test
    @DisplayName("Should return empty list when no vehicles")
    void testGetVehicleTypeDistribution_NoVehicles() {
        // Arrange
        when(vehicleRepository.countByVehicleType()).thenReturn(new ArrayList<>());

        // Act
        List<VehicleTypeDistributionResponse> distribution = dashboardService.getVehicleTypeDistribution();

        // Assert
        assertNotNull(distribution);
        assertTrue(distribution.isEmpty());
    }
}