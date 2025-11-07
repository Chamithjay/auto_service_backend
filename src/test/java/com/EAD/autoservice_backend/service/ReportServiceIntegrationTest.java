package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.RevenueOverTime;
import com.EAD.autoservice_backend.dto.ServicePopularity;
import com.EAD.autoservice_backend.dto.LeaveReport;
import com.EAD.autoservice_backend.exception.BadRequestException;
import com.EAD.autoservice_backend.model.*;
import com.EAD.autoservice_backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ReportServiceIT {

    @Autowired
    private ReportService reportService;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private AppointmentJobRepository appointmentJobRepository;
    @Autowired
    private LeaveRepository leaveRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ServiceItemRepository serviceItemRepository;
    @Autowired
    private VehicleRepository vehicleRepository;

    @BeforeEach
    void seedData() {
        // Employee for leaves
        Employee emp = new Employee();
        emp.setUsername("empPerf");
        emp.setEmail("empPerf@company.com");
        emp.setPassword("x");
        emp = userRepository.save(emp);

        // Customer & vehicle
        Customer cust = new Customer();
        cust.setUsername("cust1");
        cust.setEmail("cust1@company.com");
        cust.setPassword("y");
        cust = userRepository.save(cust);

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleName("Sedan");
        vehicle.setRegistrationNo("REG-123");
        vehicle.setVehicleType(VehicleType.CAR);
        vehicle.setModel("Model S");
        vehicle.setCustomer(cust);
        vehicle = vehicleRepository.save(vehicle);

        // Service Item
        ServiceItem svc = new ServiceItem();
        svc.setServiceItemName("Oil Change");
        svc.setVehicleType(VehicleType.CAR);
        svc.setRequiredEmployeeCount(1);
        svc.setServiceItemCost(new BigDecimal("50.00"));
        svc.setServiceItemType(ServiceItemType.SERVICE);
        svc.setEstimatedDuration(45);
        svc = serviceItemRepository.save(svc);

        // Appointment
        Appointment appt = new Appointment();
        appt.setAppointmentDate(LocalDate.now().minusDays(1));
        appt.setAppointmentStartTime(LocalDateTime.now().minusDays(1).withHour(9).withMinute(0).withSecond(0));
        appt.setAppointmentEndTime(LocalDateTime.now().minusDays(1).withHour(10).withMinute(0).withSecond(0));
        appt.setStatus(AppointmentStatus.COMPLETED);
        appt.setTotalCost(new BigDecimal("50.00"));
        appt.setVehicle(vehicle);
        appt.setVehicleName("Sedan");
        appt.setSessionType(SessionType.MORNING);
        appt = appointmentRepository.save(appt);

        // Appointment Job
        AppointmentJob job = new AppointmentJob();
        job.setAppointment(appt);
        job.setServiceItem(svc);
        job.setStartTime(LocalTime.of(9, 0));
        job.setEndTime(LocalTime.of(9, 45));
        job.setItemStatus(AppointmentStatus.COMPLETED);
        appointmentJobRepository.save(job);

        // Leave
        Leave leave = new Leave();
        leave.setEmployee(emp);
        leave.setLeaveType(LeaveType.FULLDAY);
        leave.setLeaveDate(LocalDate.now().minusDays(2));
        leave.setLeaveStatus(LeaveStatus.APPROVED);
        leaveRepository.save(leave);
    }

    @Test
    void servicePopularity_report() {
        List<ServicePopularity> popularity = reportService.getServicePopularity(7);
        assertFalse(popularity.isEmpty());
        var first = popularity.get(0);
        assertEquals("Oil Change", first.getServiceName());
        assertEquals(1L, first.getCount());
    }

    @Test
    void revenueOverTime_report() {
        List<RevenueOverTime> revenue = reportService.getRevenueOverTime(7);
        assertFalse(revenue.isEmpty());
        var first = revenue.get(0);
        assertEquals(new BigDecimal("50.00"), first.getTotalRevenue());
    }

    @Test
    void leaveReport_report() {
        List<LeaveReport> leaves = reportService.getLeaveReport(7);
        assertFalse(leaves.isEmpty());
        var first = leaves.get(0);
        assertEquals(LeaveType.FULLDAY, first.getLeaveType());
        assertEquals(1L, first.getCount());
    }

    @Test
    void invalidRange_throws() {
        assertThrows(BadRequestException.class, () -> reportService.getServicePopularity(-1));
        assertThrows(BadRequestException.class, () -> reportService.getRevenueOverTime(400));
    }
}
