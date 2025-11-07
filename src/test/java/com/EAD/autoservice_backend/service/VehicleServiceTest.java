package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.VehicleRequest;
import com.EAD.autoservice_backend.dto.VehicleResponse;
import com.EAD.autoservice_backend.exception.ResourceNotFoundException;
import com.EAD.autoservice_backend.exception.UserAlreadyExistsException;
import com.EAD.autoservice_backend.model.Customer;
import com.EAD.autoservice_backend.model.Vehicle;
import com.EAD.autoservice_backend.model.VehicleType;
import com.EAD.autoservice_backend.repository.CustomerRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for VehicleService using JUnit5 and Mockito
 */
@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private Customer testCustomer;
    private Vehicle testVehicle;
    private VehicleRequest testVehicleRequest;

    @BeforeEach
    void setUp() {
        // Initialize test customer
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setUsername("testuser");
        testCustomer.setEmail("test@example.com");

        // Initialize test vehicle
        testVehicle = new Vehicle();
        testVehicle.setVehicleId(1L);
        testVehicle.setVehicleName("Toyota Camry");
        testVehicle.setRegistrationNo("ABC-1234");
        testVehicle.setVehicleType(VehicleType.CAR);
        testVehicle.setModel("2020");
        testVehicle.setCustomer(testCustomer);
        testVehicle.setCreatedAt(LocalDateTime.now());
        testVehicle.setUpdatedAt(LocalDateTime.now());

        // Initialize test vehicle request
        testVehicleRequest = new VehicleRequest();
        testVehicleRequest.setVehicleName("Toyota Camry");
        testVehicleRequest.setRegistrationNo("ABC-1234");
        testVehicleRequest.setVehicleType(VehicleType.CAR);
        testVehicleRequest.setModel("2020");
    }

    @Test
    void addVehicle_Success() {
        // Arrange
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(vehicleRepository.existsByRegistrationNoAndCustomerId("ABC-1234", 1L)).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);

        // Act
        VehicleResponse response = vehicleService.addVehicle("testuser", testVehicleRequest);

        // Assert
        assertNotNull(response);
        assertEquals("Toyota Camry", response.getVehicleName());
        assertEquals("ABC-1234", response.getRegistrationNo());
        assertEquals(VehicleType.CAR, response.getVehicleType());
        assertEquals("2020", response.getModel());
        
        verify(customerRepository, times(1)).findByUsername("testuser");
        verify(vehicleRepository, times(1)).existsByRegistrationNoAndCustomerId("ABC-1234", 1L);
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void addVehicle_CustomerNotFound_ThrowsException() {
        // Arrange
        when(customerRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> vehicleService.addVehicle("nonexistent", testVehicleRequest)
        );
        
        assertEquals("Customer not found with username: nonexistent", exception.getMessage());
        verify(customerRepository, times(1)).findByUsername("nonexistent");
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    void addVehicle_DuplicateRegistrationNumber_ThrowsException() {
        // Arrange
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(vehicleRepository.existsByRegistrationNoAndCustomerId("ABC-1234", 1L)).thenReturn(true);

        // Act & Assert
        UserAlreadyExistsException exception = assertThrows(
            UserAlreadyExistsException.class,
            () -> vehicleService.addVehicle("testuser", testVehicleRequest)
        );
        
        assertTrue(exception.getMessage().contains("ABC-1234"));
        assertTrue(exception.getMessage().contains("already exists"));
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    void getCustomerVehicles_Success() {
        // Arrange
        Vehicle vehicle2 = new Vehicle();
        vehicle2.setVehicleId(2L);
        vehicle2.setVehicleName("Honda Civic");
        vehicle2.setRegistrationNo("XYZ-5678");
        vehicle2.setVehicleType(VehicleType.CAR);
        vehicle2.setModel("2021");
        vehicle2.setCustomer(testCustomer);
        vehicle2.setCreatedAt(LocalDateTime.now());
        vehicle2.setUpdatedAt(LocalDateTime.now());

        List<Vehicle> vehicles = Arrays.asList(testVehicle, vehicle2);

        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(vehicleRepository.findByCustomerId(1L)).thenReturn(vehicles);

        // Act
        List<VehicleResponse> response = vehicleService.getCustomerVehicles("testuser");

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Toyota Camry", response.get(0).getVehicleName());
        assertEquals("Honda Civic", response.get(1).getVehicleName());
        
        verify(customerRepository, times(1)).findByUsername("testuser");
        verify(vehicleRepository, times(1)).findByCustomerId(1L);
    }

    @Test
    void getCustomerVehicles_CustomerNotFound_ThrowsException() {
        // Arrange
        when(customerRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
            ResourceNotFoundException.class,
            () -> vehicleService.getCustomerVehicles("nonexistent")
        );
        
        verify(customerRepository, times(1)).findByUsername("nonexistent");
        verify(vehicleRepository, never()).findByCustomerId(anyLong());
    }

    @Test
    void getVehicleById_Success() {
        // Arrange
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(vehicleRepository.findByVehicleIdAndCustomerId(1L, 1L)).thenReturn(Optional.of(testVehicle));

        // Act
        VehicleResponse response = vehicleService.getVehicleById("testuser", 1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getVehicleId());
        assertEquals("Toyota Camry", response.getVehicleName());
        assertEquals("ABC-1234", response.getRegistrationNo());
        
        verify(customerRepository, times(1)).findByUsername("testuser");
        verify(vehicleRepository, times(1)).findByVehicleIdAndCustomerId(1L, 1L);
    }

    @Test
    void getVehicleById_VehicleNotFound_ThrowsException() {
        // Arrange
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(vehicleRepository.findByVehicleIdAndCustomerId(999L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> vehicleService.getVehicleById("testuser", 999L)
        );
        
        assertEquals("Vehicle not found with ID: 999", exception.getMessage());
        verify(vehicleRepository, times(1)).findByVehicleIdAndCustomerId(999L, 1L);
    }

    @Test
    void updateVehicle_Success() {
        // Arrange
        VehicleRequest updateRequest = new VehicleRequest();
        updateRequest.setVehicleName("Toyota Camry Updated");
        updateRequest.setRegistrationNo("ABC-1234");
        updateRequest.setVehicleType(VehicleType.CAR);
        updateRequest.setModel("2021");

        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(vehicleRepository.findByVehicleIdAndCustomerId(1L, 1L)).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);

        // Act
        VehicleResponse response = vehicleService.updateVehicle("testuser", 1L, updateRequest);

        // Assert
        assertNotNull(response);
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void updateVehicle_DuplicateRegistrationNumber_ThrowsException() {
        // Arrange
        VehicleRequest updateRequest = new VehicleRequest();
        updateRequest.setVehicleName("Toyota Camry");
        updateRequest.setRegistrationNo("XYZ-5678"); // Different registration number
        updateRequest.setVehicleType(VehicleType.CAR);
        updateRequest.setModel("2020");

        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(vehicleRepository.findByVehicleIdAndCustomerId(1L, 1L)).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.existsByRegistrationNoAndCustomerIdAndVehicleIdNot("XYZ-5678", 1L, 1L)).thenReturn(true);

        // Act & Assert
        UserAlreadyExistsException exception = assertThrows(
            UserAlreadyExistsException.class,
            () -> vehicleService.updateVehicle("testuser", 1L, updateRequest)
        );
        
        assertTrue(exception.getMessage().contains("XYZ-5678"));
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    void updateVehicle_VehicleNotFound_ThrowsException() {
        // Arrange
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(vehicleRepository.findByVehicleIdAndCustomerId(999L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
            ResourceNotFoundException.class,
            () -> vehicleService.updateVehicle("testuser", 999L, testVehicleRequest)
        );
        
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    void deleteVehicle_Success() {
        // Arrange
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(vehicleRepository.findByVehicleIdAndCustomerId(1L, 1L)).thenReturn(Optional.of(testVehicle));
        doNothing().when(vehicleRepository).delete(testVehicle);

        // Act
        vehicleService.deleteVehicle("testuser", 1L);

        // Assert
        verify(customerRepository, times(1)).findByUsername("testuser");
        verify(vehicleRepository, times(1)).findByVehicleIdAndCustomerId(1L, 1L);
        verify(vehicleRepository, times(1)).delete(testVehicle);
    }

    @Test
    void deleteVehicle_VehicleNotFound_ThrowsException() {
        // Arrange
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(vehicleRepository.findByVehicleIdAndCustomerId(999L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> vehicleService.deleteVehicle("testuser", 999L)
        );
        
        assertEquals("Vehicle not found with ID: 999", exception.getMessage());
        verify(vehicleRepository, never()).delete(any(Vehicle.class));
    }

    @Test
    void deleteVehicle_CustomerNotFound_ThrowsException() {
        // Arrange
        when(customerRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
            ResourceNotFoundException.class,
            () -> vehicleService.deleteVehicle("nonexistent", 1L)
        );
        
        verify(vehicleRepository, never()).delete(any(Vehicle.class));
    }
}
