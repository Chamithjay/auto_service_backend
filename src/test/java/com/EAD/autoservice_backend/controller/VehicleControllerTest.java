package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.VehicleRequest;
import com.EAD.autoservice_backend.dto.VehicleResponse;
import com.EAD.autoservice_backend.exception.ResourceNotFoundException;
import com.EAD.autoservice_backend.exception.UserAlreadyExistsException;
import com.EAD.autoservice_backend.model.VehicleType;
import com.EAD.autoservice_backend.service.VehicleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for VehicleController using JUnit5, Mockito and MockMvc
 */
@ExtendWith(MockitoExtension.class)
class VehicleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VehicleService vehicleService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private VehicleController vehicleController;

    private ObjectMapper objectMapper;
    private VehicleRequest vehicleRequest;
    private VehicleResponse vehicleResponse;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(vehicleController).build();
        objectMapper = new ObjectMapper();
        
        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.format(DATE_TIME_FORMATTER);

        when(authentication.getName()).thenReturn("testuser");

        vehicleRequest = new VehicleRequest();
        vehicleRequest.setVehicleName("Toyota Camry");
        vehicleRequest.setRegistrationNo("ABC-1234");
        vehicleRequest.setVehicleType(VehicleType.CAR);
        vehicleRequest.setModel("2020");

        vehicleResponse = new VehicleResponse();
        vehicleResponse.setVehicleId(1L);
        vehicleResponse.setVehicleName("Toyota Camry");
        vehicleResponse.setRegistrationNo("ABC-1234");
        vehicleResponse.setVehicleType(VehicleType.CAR);
        vehicleResponse.setModel("2020");
        vehicleResponse.setCreatedAt(formattedDateTime);
        vehicleResponse.setUpdatedAt(formattedDateTime);
    }

    @Test
    void addVehicle_Success() throws Exception {
        // Arrange
        when(vehicleService.addVehicle(eq("testuser"), any(VehicleRequest.class)))
            .thenReturn(vehicleResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/customer/vehicles")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vehicleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.vehicleId").value(1))
                .andExpect(jsonPath("$.vehicleName").value("Toyota Camry"))
                .andExpect(jsonPath("$.registrationNo").value("ABC-1234"))
                .andExpect(jsonPath("$.vehicleType").value("CAR"))
                .andExpect(jsonPath("$.model").value("2020"));

        verify(vehicleService, times(1)).addVehicle(eq("testuser"), any(VehicleRequest.class));
    }

    @Test
    void addVehicle_CustomerNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        when(vehicleService.addVehicle(eq("testuser"), any(VehicleRequest.class)))
            .thenThrow(new ResourceNotFoundException("Customer not found"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/customer/vehicles")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vehicleRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Customer not found"));

        verify(vehicleService, times(1)).addVehicle(eq("testuser"), any(VehicleRequest.class));
    }

    @Test
    void addVehicle_DuplicateRegistrationNumber_ReturnsConflict() throws Exception {
        // Arrange
        when(vehicleService.addVehicle(eq("testuser"), any(VehicleRequest.class)))
            .thenThrow(new UserAlreadyExistsException("Vehicle already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/customer/vehicles")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vehicleRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Vehicle already exists"));
    }

    @Test
    void getAllVehicles_Success() throws Exception {
        // Arrange
        VehicleResponse vehicle2 = new VehicleResponse();
        vehicle2.setVehicleId(2L);
        vehicle2.setVehicleName("Honda Civic");
        vehicle2.setRegistrationNo("XYZ-5678");
        vehicle2.setVehicleType(VehicleType.CAR);
        vehicle2.setModel("2021");
        vehicle2.setCreatedAt(LocalDateTime.now().format(DATE_TIME_FORMATTER));
        vehicle2.setUpdatedAt(LocalDateTime.now().format(DATE_TIME_FORMATTER));

        List<VehicleResponse> vehicles = Arrays.asList(vehicleResponse, vehicle2);

        when(vehicleService.getCustomerVehicles("testuser")).thenReturn(vehicles);

        // Act & Assert
        mockMvc.perform(get("/api/v1/customer/vehicles")
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].vehicleName").value("Toyota Camry"))
                .andExpect(jsonPath("$[1].vehicleName").value("Honda Civic"));

        verify(vehicleService, times(1)).getCustomerVehicles("testuser");
    }

    @Test
    void getAllVehicles_CustomerNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        when(vehicleService.getCustomerVehicles("testuser"))
            .thenThrow(new ResourceNotFoundException("Customer not found"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/customer/vehicles")
                .principal(authentication))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Customer not found"));
    }

    @Test
    void getVehicleById_Success() throws Exception {
        // Arrange
        when(vehicleService.getVehicleById("testuser", 1L)).thenReturn(vehicleResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/customer/vehicles/1")
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleId").value(1))
                .andExpect(jsonPath("$.vehicleName").value("Toyota Camry"))
                .andExpect(jsonPath("$.registrationNo").value("ABC-1234"));

        verify(vehicleService, times(1)).getVehicleById("testuser", 1L);
    }

    @Test
    void getVehicleById_VehicleNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        when(vehicleService.getVehicleById("testuser", 999L))
            .thenThrow(new ResourceNotFoundException("Vehicle not found with ID: 999"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/customer/vehicles/999")
                .principal(authentication))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Vehicle not found with ID: 999"));
    }

    @Test
    void updateVehicle_Success() throws Exception {
        // Arrange
        VehicleRequest updateRequest = new VehicleRequest();
        updateRequest.setVehicleName("Toyota Camry Updated");
        updateRequest.setRegistrationNo("ABC-1234");
        updateRequest.setVehicleType(VehicleType.CAR);
        updateRequest.setModel("2021");

        VehicleResponse updatedResponse = new VehicleResponse();
        updatedResponse.setVehicleId(1L);
        updatedResponse.setVehicleName("Toyota Camry Updated");
        updatedResponse.setRegistrationNo("ABC-1234");
        updatedResponse.setVehicleType(VehicleType.CAR);
        updatedResponse.setModel("2021");
        updatedResponse.setCreatedAt(LocalDateTime.now().format(DATE_TIME_FORMATTER));
        updatedResponse.setUpdatedAt(LocalDateTime.now().format(DATE_TIME_FORMATTER));

        when(vehicleService.updateVehicle(eq("testuser"), eq(1L), any(VehicleRequest.class)))
            .thenReturn(updatedResponse);

        // Act & Assert
        mockMvc.perform(put("/api/v1/customer/vehicles/1")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleName").value("Toyota Camry Updated"))
                .andExpect(jsonPath("$.model").value("2021"));

        verify(vehicleService, times(1)).updateVehicle(eq("testuser"), eq(1L), any(VehicleRequest.class));
    }

    @Test
    void updateVehicle_VehicleNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        when(vehicleService.updateVehicle(eq("testuser"), eq(999L), any(VehicleRequest.class)))
            .thenThrow(new ResourceNotFoundException("Vehicle not found"));

        // Act & Assert
        mockMvc.perform(put("/api/v1/customer/vehicles/999")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vehicleRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Vehicle not found"));
    }

    @Test
    void updateVehicle_DuplicateRegistrationNumber_ReturnsConflict() throws Exception {
        // Arrange
        when(vehicleService.updateVehicle(eq("testuser"), eq(1L), any(VehicleRequest.class)))
            .thenThrow(new UserAlreadyExistsException("Duplicate registration number"));

        // Act & Assert
        mockMvc.perform(put("/api/v1/customer/vehicles/1")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vehicleRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Duplicate registration number"));
    }

    @Test
    void deleteVehicle_Success() throws Exception {
        // Arrange
        doNothing().when(vehicleService).deleteVehicle("testuser", 1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/customer/vehicles/1")
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Vehicle deleted successfully"));

        verify(vehicleService, times(1)).deleteVehicle("testuser", 1L);
    }

    @Test
    void deleteVehicle_VehicleNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Vehicle not found"))
            .when(vehicleService).deleteVehicle("testuser", 999L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/customer/vehicles/999")
                .principal(authentication))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Vehicle not found"));
    }
}
