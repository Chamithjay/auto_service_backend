package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.ServiceItemRequest;
import com.EAD.autoservice_backend.dto.UserCreateRequest;
import com.EAD.autoservice_backend.dto.UserCreateResponse;
import com.EAD.autoservice_backend.dto.UserUpdateRequest;
import com.EAD.autoservice_backend.exception.BadRequestException;
import com.EAD.autoservice_backend.exception.ResourceConflictException;
import com.EAD.autoservice_backend.exception.ResourceNotFoundException;
import com.EAD.autoservice_backend.model.*;
import com.EAD.autoservice_backend.repository.ServiceItemRepository;
import com.EAD.autoservice_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private ServiceItemRepository serviceItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminService adminService;

    private ServiceItem serviceItem;

    @BeforeEach
    void setUp() {
        serviceItem = new ServiceItem();
        serviceItem.setServiceItemId(1L);
        serviceItem.setServiceItemName("Oil Change");
        serviceItem.setServiceItemCost(BigDecimal.valueOf(50.0));
        serviceItem.setVehicleType(VehicleType.CAR);
        serviceItem.setRequiredEmployeeCount(1);
        serviceItem.setServiceItemType(ServiceItemType.SERVICE);
        serviceItem.setEstimatedDuration(45);
    }

    // ----------------- ServiceItem Tests -----------------

    @Test
    void testGetServiceById_Success() {
        when(serviceItemRepository.findById(1L)).thenReturn(Optional.of(serviceItem));

        ServiceItem found = adminService.getServiceById(1L);

        assertNotNull(found);
        assertEquals("Oil Change", found.getServiceItemName());
        assertEquals(VehicleType.CAR, found.getVehicleType());
        verify(serviceItemRepository, times(1)).findById(1L);
    }

    @Test
    void testGetServiceById_NotFound() {
        when(serviceItemRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> adminService.getServiceById(99L));
        assertEquals("ServiceItem not found with id: 99", ex.getMessage());
        verify(serviceItemRepository).findById(99L);
    }

    @Test
    void testCreateServiceItem_Success() {
        ServiceItemRequest request = new ServiceItemRequest("Tire Rotation", "CAR", 1, BigDecimal.valueOf(45), "SERVICE", 60);

        ServiceItem savedItem = new ServiceItem();
        savedItem.setServiceItemId(2L);
        savedItem.setServiceItemName("Tire Rotation");
        savedItem.setVehicleType(VehicleType.CAR);
        savedItem.setRequiredEmployeeCount(1);
        savedItem.setServiceItemType(ServiceItemType.SERVICE);
        savedItem.setEstimatedDuration(60);
        savedItem.setServiceItemCost(BigDecimal.valueOf(45));

        when(serviceItemRepository.save(any(ServiceItem.class))).thenReturn(savedItem);

        ServiceItem result = adminService.createServiceItem(request);

        assertEquals(2L, result.getServiceItemId());
        assertEquals("Tire Rotation", result.getServiceItemName());
        verify(serviceItemRepository).save(any(ServiceItem.class));
    }

    @Test
    void testUpdateService_Success() {
        Long id = 1L;
        when(serviceItemRepository.findById(id)).thenReturn(Optional.of(serviceItem));
        when(serviceItemRepository.save(any(ServiceItem.class))).thenAnswer(inv -> inv.getArgument(0));

        ServiceItemRequest request = new ServiceItemRequest("Updated Service", "CAR", 2, BigDecimal.valueOf(70), "SERVICE", 50);

        ServiceItem updated = adminService.updateService(id, request);

        assertEquals("Updated Service", updated.getServiceItemName());
        assertEquals(2, updated.getRequiredEmployeeCount());
        assertEquals(BigDecimal.valueOf(70), updated.getServiceItemCost());
        verify(serviceItemRepository).save(any(ServiceItem.class));
    }

    @Test
    void testUpdateService_NotFound() {
        when(serviceItemRepository.findById(123L)).thenReturn(Optional.empty());
        ServiceItemRequest request = new ServiceItemRequest("n","CAR",1, BigDecimal.TEN,"SERVICE",10);

        assertThrows(ResourceNotFoundException.class, () -> adminService.updateService(123L, request));
        verify(serviceItemRepository, never()).save(any());
    }

    @Test
    void testDeleteService_Success() {
        when(serviceItemRepository.existsById(5L)).thenReturn(true);
        adminService.deleteService(5L);
        verify(serviceItemRepository).deleteById(5L);
    }

    @Test
    void testDeleteService_NotFound() {
        when(serviceItemRepository.existsById(5L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> adminService.deleteService(5L));
        verify(serviceItemRepository, never()).deleteById(anyLong());
    }

    // ----------------- User CRUD Tests -----------------

    @Test
    void testCreateUser_Admin_Success() {
        UserCreateRequest req = new UserCreateRequest("alice", "pass123", "a@b.com", "ADMIN");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("ENC(pass123)");

        Admin saved = new Admin();
        saved.setId(1L);
        saved.setUsername("alice");
        saved.setEmail("a@b.com");
        saved.setPassword("ENC(pass123)");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserCreateResponse resp = adminService.createUser(req);

        assertEquals(1L, resp.id());
        assertEquals("alice", resp.username());
        assertEquals("a@b.com", resp.email());
        assertEquals("ADMIN", resp.role());
    }

    @Test
    void testCreateUser_InvalidRole_Throws() {
        UserCreateRequest req = new UserCreateRequest("bob", "pw", "b@c.com", "MANAGER");
        when(userRepository.findByUsername("bob")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("b@c.com")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> adminService.createUser(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testCreateUser_BlankPassword_Throws() {
        UserCreateRequest req = new UserCreateRequest("bob", "   ", "b@c.com", "ADMIN");
        when(userRepository.findByUsername("bob")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("b@c.com")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> adminService.createUser(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testCreateUser_ConflictUsername() {
        User existing = new User(); existing.setId(1L);
        when(userRepository.findByUsername("dup")).thenReturn(Optional.of(existing));
        UserCreateRequest req = new UserCreateRequest("dup","pw","x@y.com","EMPLOYEE");

        assertThrows(ResourceConflictException.class, () -> adminService.createUser(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testCreateUser_ConflictEmail() {
        when(userRepository.findByUsername("ok")).thenReturn(Optional.empty());
        User existing = new User(); existing.setId(1L);
        when(userRepository.findByEmail("dup@x.com")).thenReturn(Optional.of(existing));
        UserCreateRequest req = new UserCreateRequest("ok","pw","dup@x.com","EMPLOYEE");

        assertThrows(ResourceConflictException.class, () -> adminService.createUser(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateUser_Success() {
        Admin existing = new Admin(); existing.setId(5L); existing.setUsername("old"); existing.setEmail("old@x.com");
        when(userRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(userRepository.findByUsername("new")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("new@x.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserUpdateRequest req = new UserUpdateRequest("new","new@x.com", null);
        var resp = adminService.updateUser(5L, req);

        assertEquals("new", resp.username());
        assertEquals("new@x.com", resp.email());
    }

    @Test
    void testUpdateUser_ConflictUsername() {
        Admin existing = new Admin(); existing.setId(5L);
        when(userRepository.findById(5L)).thenReturn(Optional.of(existing));
        User other = new User(); other.setId(7L);
        when(userRepository.findByUsername("taken")).thenReturn(Optional.of(other));

        UserUpdateRequest req = new UserUpdateRequest("taken","free@x.com",null);
        assertThrows(ResourceConflictException.class, () -> adminService.updateUser(5L, req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateUser_ConflictEmail() {
        Admin existing = new Admin(); existing.setId(5L);
        when(userRepository.findById(5L)).thenReturn(Optional.of(existing));
        User other = new User(); other.setId(7L);
        when(userRepository.findByEmail("taken@x.com")).thenReturn(Optional.of(other));

        UserUpdateRequest req = new UserUpdateRequest("free","taken@x.com",null);
        assertThrows(ResourceConflictException.class, () -> adminService.updateUser(5L, req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.existsById(3L)).thenReturn(true);
        adminService.deleteUser(3L);
        verify(userRepository).deleteById(3L);
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userRepository.existsById(3L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> adminService.deleteUser(3L));
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void testGetAllUsers() {
        Admin a = new Admin(); a.setId(1L); a.setUsername("a"); a.setEmail("a@x.com");
        Employee e = new Employee(); e.setId(2L); e.setUsername("e"); e.setEmail("e@x.com");
        when(userRepository.findAll()).thenReturn(List.of(a,e));

        var users = adminService.getAllUsers();
        assertEquals(2, users.size());
        assertEquals("ADMIN", users.get(0).role());
        assertEquals("EMPLOYEE", users.get(1).role());
    }
}
