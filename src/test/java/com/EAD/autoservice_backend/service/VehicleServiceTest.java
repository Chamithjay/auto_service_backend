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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit Tests for VehicleService
 * Uses JUnit5 and Mockito to test vehicle service operations
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleService Unit Tests")
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private Customer testCustomer;
    private Vehicle testVehicle;
    private VehicleRequest vehicleRequest;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    void setUp() {
        // Setup test customer
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setUsername("testuser");
        testCustomer.setEmail("test@example.com");

        // Setup test vehicle
        testVehicle = new Vehicle();
        testVehicle.setVehicleId(1L);
        testVehicle.setVehicleName("Toyota Camry");
        testVehicle.setRegistrationNo("ABC-1234");
        testVehicle.setVehicleType(VehicleType.CAR);
        testVehicle.setModel("2020");
        testVehicle.setCustomer(testCustomer);
        testVehicle.setCreatedAt(LocalDateTime.now());
        testVehicle.setUpdatedAt(LocalDateTime.now());

        // Setup vehicle request
        vehicleRequest = new VehicleRequest();
        vehicleRequest.setVehicleName("Toyota Camry");
        vehicleRequest.setRegistrationNo("ABC-1234");
        vehicleRequest.setVehicleType(VehicleType.CAR);
        vehicleRequest.setModel("2020");
    }

    @Test
    @DisplayName("Add Vehicle - Success")
    void addVehicle_Success() {
        // Arrange
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(vehicleRepository.existsByRegistrationNoAndCustomerId(anyString(), anyLong())).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);

        // Act
        VehicleResponse response = vehicleService.addVehicle("testuser", vehicleRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getVehicleId());
        assertEquals("Toyota Camry", response.getVehicleName());
        assertEquals("ABC-1234", response.getRegistrationNo());
        assertEquals(VehicleType.CAR, response.getVehicleType());
        assertEquals("2020", response.getModel());

        verify(customerRepository, times(1)).findByUsername("testuser");
        verify(vehicleRepository, times(1)).existsByRegistrationNoAndCustomerId("ABC-1234", 1L);
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Add Vehicle - Customer Not Found")
    void addVehicle_CustomerNotFound() {
        // Arrange
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> vehicleService.addVehicle("testuser", vehicleRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer not found with username: testuser");

        verify(customerRepository, times(1)).findByUsername("testuser");
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Add Vehicle - Duplicate Registration Number")
    void addVehicle_DuplicateRegistrationNumber() {
        // Arrange
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(vehicleRepository.existsByRegistrationNoAndCustomerId("ABC-1234", 1L)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> vehicleService.addVehicle("testuser", vehicleRequest))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("Vehicle with registration number 'ABC-1234' already exists for this customer");

        verify(customerRepository, times(1)).findByUsername("testuser");
        verify(vehicleRepository, times(1)).existsByRegistrationNoAndCustomerId("ABC-1234", 1L);
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Add Vehicle - Verify Vehicle Properties Are Set Correctly")
    void addVehicle_VerifyVehicleProperties() {
        // Arrange
        ArgumentCaptor<Vehicle> vehicleCaptor = ArgumentCaptor.forClass(Vehicle.class);
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(vehicleRepository.existsByRegistrationNoAndCustomerId(anyString(), anyLong())).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);

        // Act
        vehicleService.addVehicle("testuser", vehicleRequest);

        // Assert
        verify(vehicleRepository).save(vehicleCaptor.capture());
        Vehicle capturedVehicle = vehicleCaptor.getValue();
        
        assertEquals("Toyota Camry", capturedVehicle.getVehicleName());
        assertEquals("ABC-1234", capturedVehicle.getRegistrationNo());
        assertEquals(VehicleType.CAR, capturedVehicle.getVehicleType());
        assertEquals("2020", capturedVehicle.getModel());
        assertEquals(testCustomer, capturedVehicle.getCustomer());
        assertNotNull(capturedVehicle.getCreatedAt());
        assertNotNull(capturedVehicle.getUpdatedAt());
    }

    @Test
    @DisplayName("Get Customer Vehicles - Success")
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
        List<VehicleResponse> responses = vehicleService.getCustomerVehicles("testuser");

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        
        VehicleResponse firstVehicle = responses.get(0);
        assertEquals(1L, firstVehicle.getVehicleId());
        assertEquals("Toyota Camry", firstVehicle.getVehicleName());
        
        VehicleResponse secondVehicle = responses.get(1);
        assertEquals(2L, secondVehicle.getVehicleId());
        assertEquals("Honda Civic", secondVehicle.getVehicleName());

        verify(customerRepository, times(1)).findByUsername("testuser");
        verify(vehicleRepository, times(1)).findByCustomerId(1L);
    }

    @Test
    @DisplayName("Get Customer Vehicles - Empty List")
    void getCustomerVehicles_EmptyList() {
        // Arrange
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(vehicleRepository.findByCustomerId(1L)).thenReturn(Arrays.asList());

        // Act
        List<VehicleResponse> responses = vehicleService.getCustomerVehicles("testuser");

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(customerRepository, times(1)).findByUsername("testuser");
        verify(vehicleRepository, times(1)).findByCustomerId(1L);
    }

    @Test
    @DisplayName("Get Customer Vehicles - Customer Not Found")
    void getCustomerVehicles_CustomerNotFound() {
        // Arrange
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> vehicleService.getCustomerVehicles("testuser"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer not found with username: testuser");

        verify(customerRepository, times(1)).findByUsername("testuser");
        verify(vehicleRepository, never()).findByCustomerId(anyLong());
    }

    @Test
    @DisplayName("Get Vehicle By Id - Success")
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
        assertEquals(VehicleType.CAR, response.getVehicleType());

        verify(customerRepository, times(1)).findByUsername("testuser");
        verify(vehicleRepository, times(1)).findByVehicleIdAndCustomerId(1L, 1L);
    }

    @Test
    @DisplayName("Get Vehicle By Id - Customer Not Found")
    void getVehicleById_CustomerNotFound() {
        // Arrange
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> vehicleService.getVehicleById("testuser", 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer not found with username: testuser");

        verify(customerRepository, times(1)).findByUsername("testuser");
        verify(vehicleRepository, never()).findByVehicleIdAndCustomerId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Get Vehicle By Id - Vehicle Not Found")
    void getVehicleById_VehicleNotFound() {
        // Arrange
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(vehicleRepository.findByVehicleIdAndCustomerId(1L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> vehicleService.getVehicleById("testuser", 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vehicle not found with ID: 1");

        verify(customerRepository, times(1)).findByUsername("testuser");
        verify(vehicleRepository, times(1)).findByVehicleIdAndCustomerId(1L, 1L);
    }

    @Test
    @DisplayName("Update Vehicle - Success")
    void updateVehicle_Success() {
        // Arrange
        VehicleRequest updateRequest = new VehicleRequest();
        updateRequest.setVehicleName("Toyota Camry Updated");
        updateRequest.setRegistrationNo("ABC-1234");
        updateRequest.setVehicleType(VehicleType.CAR);
        updateRequest.setModel("2022");

        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(vehicleRepository.findByVehicleIdAndCustomerId(1L, 1L)).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);

        // Act
        VehicleResponse response = vehicleService.updateVehicle("testuser", 1L, updateRequest);

        // Assert
        assertNotNull(response);
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
        verify(customerRepository, times(1)).findByUsername("testuser");
        verify(vehicleRepository, times(1)).findByVehicleIdAndCustomerId(1L, 1L);
    }

    @Test
    @DisplayName("Update Vehicle - Change Registration Number Successfully")
    void updateVehicle_ChangeRegistrationNumberSuccessfully() {
        // Arrange
        VehicleRequest updateRequest = new VehicleRequest();
        updateRequest.setVehicleName("Toyota Camry");
        updateRequest.setRegistrationNo("NEW-5678");
        updateRequest.setVehicleType(VehicleType.CAR);
        updateRequest.setModel("2020");

        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(vehicleRepository.findByVehicleIdAndCustomerId(1L, 1L)).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.existsByRegistrationNoAndCustomerIdAndVehicleIdNot("NEW-5678", 1L, 1L)).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);

        // Act
        VehicleResponse response = vehicleService.updateVehicle("testuser", 1L, updateRequest);

        // Assert
        assertNotNull(response);
        verify(vehicleRepository, times(1)).existsByRegistrationNoAndCustomerIdAndVehicleIdNot("NEW-5678", 1L, 1L);
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Update Vehicle - Duplicate Registration Number")
    void updateVehicle_DuplicateRegistrationNumber() {
        // Arrange
        VehicleRequest updateRequest = new VehicleRequest();
        updateRequest.setVehicleName("Toyota Camry");
        updateRequest.setRegistrationNo("XYZ-9999");
        updateRequest.setVehicleType(VehicleType.CAR);
        updateRequest.setModel("2020");

        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(vehicleRepository.findByVehicleIdAndCustomerId(1L, 1L)).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.existsByRegistrationNoAndCustomerIdAndVehicleIdNot("XYZ-9999", 1L, 1L)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> vehicleService.updateVehicle("testuser", 1L, updateRequest))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("Vehicle with registration number 'XYZ-9999' already exists for this customer");

        verify(vehicleRepository, times(1)).existsByRegistrationNoAndCustomerIdAndVehicleIdNot("XYZ-9999", 1L, 1L);
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Update Vehicle - Customer Not Found")
    void updateVehicle_CustomerNotFound() {
        // Arrange
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> vehicleService.updateVehicle("testuser", 1L, vehicleRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer not found with username: testuser");

        verify(customerRepository, times(1)).findByUsername("testuser");
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Update Vehicle - Vehicle Not Found")
    void updateVehicle_VehicleNotFound() {
        // Arrange
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(vehicleRepository.findByVehicleIdAndCustomerId(1L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> vehicleService.updateVehicle("testuser", 1L, vehicleRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vehicle not found with ID: 1");

        verify(customerRepository, times(1)).findByUsername("testuser");
        verify(vehicleRepository, times(1)).findByVehicleIdAndCustomerId(1L, 1L);
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Update Vehicle - Verify Updated Properties")
    void updateVehicle_VerifyUpdatedProperties() {
        // Arrange
        ArgumentCaptor<Vehicle> vehicleCaptor = ArgumentCaptor.forClass(Vehicle.class);
        VehicleRequest updateRequest = new VehicleRequest();
        updateRequest.setVehicleName("Updated Vehicle Name");
        updateRequest.setRegistrationNo("ABC-1234");
        updateRequest.setVehicleType(VehicleType.VAN);
        updateRequest.setModel("2023");

        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(vehicleRepository.findByVehicleIdAndCustomerId(1L, 1L)).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);

        // Act
        vehicleService.updateVehicle("testuser", 1L, updateRequest);

        // Assert
        verify(vehicleRepository).save(vehicleCaptor.capture());
        Vehicle capturedVehicle = vehicleCaptor.getValue();
        
        assertEquals("Updated Vehicle Name", capturedVehicle.getVehicleName());
        assertEquals("ABC-1234", capturedVehicle.getRegistrationNo());
        assertEquals(VehicleType.VAN, capturedVehicle.getVehicleType());
        assertEquals("2023", capturedVehicle.getModel());
    }

    @Test
    @DisplayName("Delete Vehicle - Success")
    void deleteVehicle_Success() {
        // Arrange
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(vehicleRepository.findByVehicleIdAndCustomerId(1L, 1L)).thenReturn(Optional.of(testVehicle));
        doNothing().when(vehicleRepository).delete(any(Vehicle.class));

        // Act
        vehicleService.deleteVehicle("testuser", 1L);

        // Assert
        verify(customerRepository, times(1)).findByUsername("testuser");
        verify(vehicleRepository, times(1)).findByVehicleIdAndCustomerId(1L, 1L);
        verify(vehicleRepository, times(1)).delete(testVehicle);
    }

    @Test
    @DisplayName("Delete Vehicle - Customer Not Found")
    void deleteVehicle_CustomerNotFound() {
        // Arrange
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> vehicleService.deleteVehicle("testuser", 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer not found with username: testuser");

        verify(customerRepository, times(1)).findByUsername("testuser");
        verify(vehicleRepository, never()).delete(any(Vehicle.class));
    }

    @Test
    @DisplayName("Delete Vehicle - Vehicle Not Found")
    void deleteVehicle_VehicleNotFound() {
        // Arrange
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(vehicleRepository.findByVehicleIdAndCustomerId(1L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> vehicleService.deleteVehicle("testuser", 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vehicle not found with ID: 1");

        verify(customerRepository, times(1)).findByUsername("testuser");
        verify(vehicleRepository, times(1)).findByVehicleIdAndCustomerId(1L, 1L);
        verify(vehicleRepository, never()).delete(any(Vehicle.class));
    }

    @Test
    @DisplayName("Add Vehicle - All Vehicle Types (CAR, VAN, BUS)")
    void addVehicle_AllVehicleTypes() {
        // Test CAR
        vehicleRequest.setVehicleType(VehicleType.CAR);
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(vehicleRepository.existsByRegistrationNoAndCustomerId(anyString(), anyLong())).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);
        
        VehicleResponse carResponse = vehicleService.addVehicle("testuser", vehicleRequest);
        assertNotNull(carResponse);

        // Test VAN
        vehicleRequest.setVehicleType(VehicleType.VAN);
        testVehicle.setVehicleType(VehicleType.VAN);
        VehicleResponse vanResponse = vehicleService.addVehicle("testuser", vehicleRequest);
        assertNotNull(vanResponse);

        // Test BUS
        vehicleRequest.setVehicleType(VehicleType.BUS);
        testVehicle.setVehicleType(VehicleType.BUS);
        VehicleResponse busResponse = vehicleService.addVehicle("testuser", vehicleRequest);
        assertNotNull(busResponse);

        verify(vehicleRepository, times(3)).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Add Vehicle - With Null Model")
    void addVehicle_WithNullModel() {
        // Arrange
        vehicleRequest.setModel(null);
        testVehicle.setModel(null);
        
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(vehicleRepository.existsByRegistrationNoAndCustomerId(anyString(), anyLong())).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);

        // Act
        VehicleResponse response = vehicleService.addVehicle("testuser", vehicleRequest);

        // Assert
        assertNotNull(response);
        assertNull(response.getModel());
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Get Customer Vehicles - Verify Correct Mapping")
    void getCustomerVehicles_VerifyCorrectMapping() {
        // Arrange
        when(customerRepository.findByUsername("testuser")).thenReturn(Optional.of(testCustomer));
        when(vehicleRepository.findByCustomerId(1L)).thenReturn(Arrays.asList(testVehicle));

        // Act
        List<VehicleResponse> responses = vehicleService.getCustomerVehicles("testuser");

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        
        VehicleResponse response = responses.get(0);
        assertEquals(testVehicle.getVehicleId(), response.getVehicleId());
        assertEquals(testVehicle.getVehicleName(), response.getVehicleName());
        assertEquals(testVehicle.getRegistrationNo(), response.getRegistrationNo());
        assertEquals(testVehicle.getVehicleType(), response.getVehicleType());
        assertEquals(testVehicle.getModel(), response.getModel());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
    }
}
