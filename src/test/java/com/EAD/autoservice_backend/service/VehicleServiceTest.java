package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.VehiclesDTO;
import com.EAD.autoservice_backend.model.Customer;
import com.EAD.autoservice_backend.model.Vehicle;
import com.EAD.autoservice_backend.model.VehicleType;
import com.EAD.autoservice_backend.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle vehicle1;
    private Vehicle vehicle2;


  @BeforeEach
void setUp() {
    Customer customer1 = new Customer();
    customer1.setId(1L);

    Customer customer2 = new Customer();
    customer2.setId(2L);

    vehicle1 = new Vehicle();
    vehicle1.setVehicleId(101L);
    vehicle1.setVehicleName("Car A");
    vehicle1.setRegistrationNo("ABC123");
    vehicle1.setVehicleType(VehicleType.CAR);
    vehicle1.setModel("2023");
    vehicle1.setCreatedAt(LocalDateTime.now());
    vehicle1.setUpdatedAt(LocalDateTime.now());
    vehicle1.setCustomer(customer1);

    vehicle2 = new Vehicle();
    vehicle2.setVehicleId(102L);
    vehicle2.setVehicleName("Car B");
    vehicle2.setRegistrationNo("XYZ789");
    vehicle2.setVehicleType(VehicleType.CAR);
    vehicle2.setModel("2024");
    vehicle2.setCreatedAt(LocalDateTime.now());
    vehicle2.setUpdatedAt(LocalDateTime.now());
    vehicle2.setCustomer(customer2);
}

    @Test
    void testGetAllVehicles() {
        // Mock repository
        when(vehicleRepository.findAll()).thenReturn(Arrays.asList(vehicle1, vehicle2));

        // Call service
        List<VehiclesDTO> result = vehicleService.getAllVehicles();

        // Assertions
        assertEquals(2, result.size());

        VehiclesDTO dto1 = result.get(0);
        assertEquals(101L, dto1.getVehicleId());
        assertEquals("Car A", dto1.getVehicleName());
        assertEquals("ABC123", dto1.getRegistrationNo());
        assertEquals(VehicleType.CAR, dto1.getVehicleType());
        assertEquals("2023", dto1.getModel());
        assertEquals(1L, dto1.getCustomerId());

        VehiclesDTO dto2 = result.get(1);
        assertEquals(102L, dto2.getVehicleId());
        assertEquals("Car B", dto2.getVehicleName());
        assertEquals("XYZ789", dto2.getRegistrationNo());
        assertEquals(VehicleType.CAR, dto2.getVehicleType());
        assertEquals("2024", dto2.getModel());
        assertEquals(2L, dto2.getCustomerId());
    }
}
