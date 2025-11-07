package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.*;
import com.EAD.autoservice_backend.exception.NoAvailableEmployeeException;
import com.EAD.autoservice_backend.model.*;
import com.EAD.autoservice_backend.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @InjectMocks
    private AppointmentService appointmentService;
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private AppointmentJobRepository appointmentJobRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private JobAssignmentRepository jobAssignmentRepository;
    @Mock
    private WorkSessionRepository workSessionRepository;
    @Mock
    private ServiceItemRepository serviceItemRepository;
    @Mock
    private LeaveRepository leaveRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private VehicleRepository vehicleRepository;


    private WorkSession morningSession;
    private LocalDate date;
    private List<Employee> employees;

    @Test
    void testCalculateAppointmentDetails_noServiceItemsSelected_returnsMessage() {
        AppointmentCalculationRequest request = new AppointmentCalculationRequest();
        request.setSelectedServiceItemIds(List.of());

        AppointmentCalculationResponse response = appointmentService.calculateAppointmentDetails(request);

        assertEquals("No service items selected.", response.getMessage());
        assertNull(response.getTotalCost());
    }

    @Test
    void testCalculateAppointmentDetails_noEmployees_returnsMessage() {
        AppointmentCalculationRequest request = new AppointmentCalculationRequest();
        request.setSelectedServiceItemIds(List.of(1L, 2L));
        request.setAppointmentDate(LocalDate.now());
        request.setSessionType(SessionType.MORNING);

        // Mock service items
        ServiceItem item1 = new ServiceItem();
        item1.setServiceItemCost(BigDecimal.valueOf(50));
        ServiceItem item2 = new ServiceItem();
        item2.setServiceItemCost(BigDecimal.valueOf(30));
        when(serviceItemRepository.findAllById(request.getSelectedServiceItemIds()))
                .thenReturn(List.of(item1, item2));

        // No employees
        when(employeeRepository.findAll()).thenReturn(List.of());

        AppointmentCalculationResponse response = appointmentService.calculateAppointmentDetails(request);

        assertEquals("No employees found in the system.", response.getMessage());
    }

    @Test
    void testCalculateAppointmentDetails_allEmployeesOnLeave_returnsMessage() {
        AppointmentCalculationRequest request = new AppointmentCalculationRequest();
        request.setSelectedServiceItemIds(List.of(1L));
        request.setAppointmentDate(LocalDate.now());
        request.setSessionType(SessionType.MORNING);

        // Mock service items
        ServiceItem item = new ServiceItem();
        item.setServiceItemCost(BigDecimal.valueOf(50));
        item.setEstimatedDuration(60);
        item.setServiceItemName("Service A");
        when(serviceItemRepository.findAllById(request.getSelectedServiceItemIds()))
                .thenReturn(List.of(item));

        // Mock employees
        Employee emp1 = new Employee();
        emp1.setId(1L);
        when(employeeRepository.findAll()).thenReturn(List.of(emp1));

        // Mock leave repository
        when(leaveRepository.isEmployeeOnApprovedLeave(emp1.getId(), request.getAppointmentDate(), LeaveType.HALFDAY_MORNING))
                .thenReturn(true);

        AppointmentCalculationResponse response = appointmentService.calculateAppointmentDetails(request);

        assertEquals("No employees available for the selected date and session.", response.getMessage());
    }

    @Test
    void testCalculateAppointmentDetails_slotAvailable_returnsTotalCost() {
        AppointmentCalculationRequest request = new AppointmentCalculationRequest();
        request.setSelectedServiceItemIds(List.of(1L));
        request.setAppointmentDate(LocalDate.now());
        request.setSessionType(SessionType.MORNING);

        // Service item
        ServiceItem item = new ServiceItem();
        item.setServiceItemCost(BigDecimal.valueOf(50));
        item.setEstimatedDuration(60);
        item.setServiceItemName("Service A");
        when(serviceItemRepository.findAllById(request.getSelectedServiceItemIds()))
                .thenReturn(List.of(item));

        // Employee
        Employee emp1 = new Employee();
        emp1.setId(1L);
        when(employeeRepository.findAll()).thenReturn(List.of(emp1));

        // Employee not on leave
        when(leaveRepository.isEmployeeOnApprovedLeave(emp1.getId(), request.getAppointmentDate(), LeaveType.HALFDAY_MORNING))
                .thenReturn(false);

        // Work session
        WorkSession session = new WorkSession();
        session.setDurationHours(8);
        when(workSessionRepository.findBySessionType(SessionType.MORNING)).thenReturn(Optional.of(session));

        // Job assignment: employee has 0 used minutes
        when(jobAssignmentRepository.sumTotalDurationByDateAndEmployeeAndSession(emp1.getId(), request.getAppointmentDate(), SessionType.MORNING))
                .thenReturn(0);

        AppointmentCalculationResponse response = appointmentService.calculateAppointmentDetails(request);

        assertEquals("Slot available for the selected session.", response.getMessage());
        assertEquals(BigDecimal.valueOf(50), response.getTotalCost());
    }

    @Test
    void testCalculateAppointmentDetails_noEmployeeHasTime() {
        // Arrange
        AppointmentCalculationRequest request = new AppointmentCalculationRequest();
        request.setAppointmentDate(LocalDate.of(2025, 5, 10));
        request.setSessionType(SessionType.MORNING);
        request.setSelectedServiceItemIds(List.of(1L, 2L)); // assume two services selected

        // Mock service items
        ServiceItem item1 = new ServiceItem();
        item1.setServiceItemName("Engine Tune-Up");
        item1.setServiceItemCost(new BigDecimal("100.00"));
        item1.setEstimatedDuration(60); // 60 mins

        ServiceItem item2 = new ServiceItem();
        item2.setServiceItemName("Oil Change");
        item2.setServiceItemCost(new BigDecimal("75.00"));
        item2.setEstimatedDuration(30); // 30 mins

        Mockito.when(serviceItemRepository.findAllById(request.getSelectedServiceItemIds()))
                .thenReturn(List.of(item1, item2));

        // Mock employees
        Employee emp1 = new Employee();
        emp1.setId(1L);

        Employee emp2 = new Employee();
        emp2.setId(2L);

        Mockito.when(employeeRepository.findAll())
                .thenReturn(List.of(emp1, emp2));

        // No one is on leave
        Mockito.when(leaveRepository.isEmployeeOnApprovedLeave(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(false);

        // Session duration → assume MORNING session = 5 hours = 300 minutes
        WorkSession session = new WorkSession();
        session.setDurationHours(5.0);
        Mockito.when(workSessionRepository.findBySessionType(SessionType.MORNING))
                .thenReturn(Optional.of(session));

        // Every employee already used their full session time → 300 mins
        Mockito.when(jobAssignmentRepository.sumTotalDurationByDateAndEmployeeAndSession(
                        Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(300);

        // Act
        AppointmentCalculationResponse response = appointmentService.calculateAppointmentDetails(request);

        // Assert
        assertEquals("No available employees can take the service item: Engine Tune-Up", response.getMessage());
        assertNull(response.getTotalCost());
    }

    @Test
    void testGetNextEmployee_returnsNextAvailableEmployee() throws Exception {
        // Arrange
        Long lastEmployeeId = 1L;
        LocalDate date = LocalDate.now();
        SessionType sessionType = SessionType.MORNING;
        int newJobDuration = 60;

        Employee emp1 = new Employee();
        emp1.setId(1L);

        Employee emp2 = new Employee();
        emp2.setId(2L);

        Employee emp3 = new Employee();
        emp3.setId(3L);

        List<Employee> employees = List.of(emp1, emp2, emp3);

        // Mock hours per session (ex: 4 hours = 240 minutes)
        WorkSession session = new WorkSession();
        session.setDurationHours(4.0);
        when(workSessionRepository.findBySessionType(sessionType))
                .thenReturn(Optional.of(session));

        // emp2: already used 100 minutes → still ok (100 + 60 <= 240)
        when(jobAssignmentRepository.sumTotalDurationByDateAndEmployeeAndSession(2L, date, sessionType))
                .thenReturn(100);

        // emp1 & emp3 usage is not checked first, only emp2 must qualify first in rotation
        lenient().when(jobAssignmentRepository.sumTotalDurationByDateAndEmployeeAndSession(1L, date, sessionType))
                .thenReturn(0);
        lenient().when(jobAssignmentRepository.sumTotalDurationByDateAndEmployeeAndSession(3L, date, sessionType))
                .thenReturn(0);


        // Access private method via reflection:
        Method method = AppointmentService.class.getDeclaredMethod(
                "getNextEmployee",
                Long.class, LocalDate.class, int.class, SessionType.class, List.class
        );
        method.setAccessible(true);

        Employee result = (Employee) method.invoke(appointmentService,
                lastEmployeeId, date, newJobDuration, sessionType, employees);

        // Assert → emp2 should be selected (next in order after emp1)
        assertEquals(2L, result.getId());
    }

    @Test
    void testGetNextEmployee_noEmployeeHasTime_throwsException() throws Exception {
        Long lastEmployeeId = 1L;
        LocalDate date = LocalDate.now();
        SessionType sessionType = SessionType.MORNING;
        int newJobDuration = 60;

        Employee emp1 = new Employee(); emp1.setId(1L);
        Employee emp2 = new Employee(); emp2.setId(2L);
        List<Employee> employees = List.of(emp1, emp2);

        // Session = 120 minutes max
        WorkSession session = new WorkSession();
        session.setDurationHours(2.0);
        when(workSessionRepository.findBySessionType(sessionType))
                .thenReturn(Optional.of(session));

        // Both employees already full
        when(jobAssignmentRepository.sumTotalDurationByDateAndEmployeeAndSession(1L, date, sessionType))
                .thenReturn(120);
        when(jobAssignmentRepository.sumTotalDurationByDateAndEmployeeAndSession(2L, date, sessionType))
                .thenReturn(120);

        Method method = AppointmentService.class.getDeclaredMethod(
                "getNextEmployee",
                Long.class, LocalDate.class, int.class, SessionType.class, List.class
        );
        method.setAccessible(true);

        assertThrows(NoAvailableEmployeeException.class, () -> {
            try {
                method.invoke(appointmentService,
                        lastEmployeeId, date, newJobDuration, sessionType, employees);
            } catch (Exception e) {
                // unwrap reflection exception
                throw (Exception) e.getCause();
            }
        });
    }

    @Test
    void testCreateAppointment_successfullyCreatesAppointment() {
        // Arrange
        Long userId = 10L;

        AppointmentCreateRequest request = new AppointmentCreateRequest();
        request.setVehicleId(100L);
        request.setAppointmentDate(LocalDate.now());
        request.setSessionType(SessionType.MORNING);
        request.setSelectedServiceItemIds(List.of(1L, 2L));

        // Mock customer
        Customer customer = new Customer();
        customer.setId(userId);
        when(customerRepository.findById(userId)).thenReturn(Optional.of(customer));

        // Mock vehicle
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId(100L);
        vehicle.setVehicleName("Toyota");
        when(vehicleRepository.findByVehicleId(100L)).thenReturn(vehicle);

        // Mock service items
        ServiceItem item1 = new ServiceItem();
        item1.setServiceItemCost(BigDecimal.valueOf(50));
        item1.setEstimatedDuration(60);

        ServiceItem item2 = new ServiceItem();
        item2.setServiceItemCost(BigDecimal.valueOf(30));
        item2.setEstimatedDuration(30);

        when(serviceItemRepository.findAllById(request.getSelectedServiceItemIds()))
                .thenReturn(List.of(item1, item2));

        // Mock session leave type and employees
        Employee emp = new Employee();
        emp.setId(1L);
        when(employeeRepository.findAllByOrderByIdAsc()).thenReturn(List.of(emp));

        // Employee not on leave
        when(leaveRepository.isEmployeeOnApprovedLeave(emp.getId(), request.getAppointmentDate(), LeaveType.HALFDAY_MORNING))
                .thenReturn(false);

        // No last assigned employee
        when(jobAssignmentRepository.findLastAssignedEmployeeId()).thenReturn(Optional.empty());

        // Make getNextEmployee logic pass
        when(jobAssignmentRepository.sumTotalDurationByDateAndEmployeeAndSession(emp.getId(), request.getAppointmentDate(), SessionType.MORNING))
                .thenReturn(0);

        WorkSession session = new WorkSession();
        session.setDurationHours(5.0);
        when(workSessionRepository.findBySessionType(SessionType.MORNING))
                .thenReturn(Optional.of(session));

        // Act
        AppointmentResponse response = appointmentService.createAppointment(request, userId);

        // Assert
        assertEquals("Appointment created successfully.", response.getMessage());
        assertEquals("Toyota", response.getVehicleName());
        assertEquals(SessionType.MORNING, response.getSessionType());
        assertEquals(BigDecimal.valueOf(80), response.getTotalCost());
    }

    @Test
    void testCreateAppointment_noEmployeesAvailable_throwsException() {
        Long userId = 10L;

        AppointmentCreateRequest request = new AppointmentCreateRequest();
        request.setAppointmentDate(LocalDate.now());
        request.setSessionType(SessionType.MORNING);
        request.setVehicleId(100L);
        request.setSelectedServiceItemIds(List.of(1L));

        // Mock customer and vehicle
        when(customerRepository.findById(userId)).thenReturn(Optional.of(new Customer()));
        when(vehicleRepository.findByVehicleId(100L)).thenReturn(new Vehicle());

        // One employee but he is on leave
        Employee emp = new Employee();
        emp.setId(1L);
        when(employeeRepository.findAllByOrderByIdAsc()).thenReturn(List.of(emp));
        when(leaveRepository.isEmployeeOnApprovedLeave(emp.getId(), request.getAppointmentDate(), LeaveType.HALFDAY_MORNING))
                .thenReturn(true);

        // Mock service item
        ServiceItem item = new ServiceItem();
        item.setServiceItemCost(BigDecimal.valueOf(50));
        when(serviceItemRepository.findAllById(request.getSelectedServiceItemIds()))
                .thenReturn(List.of(item));

        // Assert exception
        assertThrows(NoAvailableEmployeeException.class, () -> {
            appointmentService.createAppointment(request, userId);
        });
    }






    // Add more @Mock if your service uses other repositories/utilities
}

