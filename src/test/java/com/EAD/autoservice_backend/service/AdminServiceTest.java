package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.ServiceItemRequest;
import com.EAD.autoservice_backend.dto.ServiceItemResponse;
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

    @Test
    void testGetServiceById_Success() {
        when(serviceItemRepository.findById(1L)).thenReturn(Optional.of(serviceItem));

        ServiceItemResponse foundService = adminService.getServiceById(1L);

        assertNotNull(foundService);
        assertEquals("Oil Change", foundService.serviceItemName());
        assertEquals(1L, foundService.serviceItemId());
        assertEquals(VehicleType.CAR, foundService.vehicleType());

        verify(serviceItemRepository, times(1)).findById(1L);
    }

    @Test
    void testGetServiceById_NotFound() {
        when(serviceItemRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> adminService.getServiceById(99L));
        assertEquals("ServiceItem not found with id: 99", exception.getMessage());
        verify(serviceItemRepository, times(1)).findById(99L);
    }

    @Test
    void testCreateServiceItem() {
        ServiceItemRequest request = new ServiceItemRequest(
                "Tire Rotation",
                "CAR",
                1,
                BigDecimal.valueOf(45.0),
                "SERVICE",
                60
        );

        ServiceItem savedItem = new ServiceItem();
        savedItem.setServiceItemId(2L);
        savedItem.setServiceItemName("Tire Rotation");
        savedItem.setVehicleType(VehicleType.CAR);
        savedItem.setRequiredEmployeeCount(1);
        savedItem.setServiceItemType(ServiceItemType.SERVICE);
        savedItem.setEstimatedDuration(60);
        savedItem.setServiceItemCost(BigDecimal.valueOf(45.0));

        when(serviceItemRepository.save(any(ServiceItem.class))).thenReturn(savedItem);

        ServiceItemResponse createdItem = adminService.createServiceItem(request);

        assertNotNull(createdItem);
        assertEquals(2L, createdItem.serviceItemId());
        assertEquals("Tire Rotation", createdItem.serviceItemName());
        assertEquals(60, createdItem.estimatedDuration());

        verify(serviceItemRepository, times(1)).save(any(ServiceItem.class));
    }

    @Test
    void testUpdateService_Success() {
        Long id = 10L;
        ServiceItem existing = new ServiceItem();
        existing.setServiceItemId(id);
        existing.setServiceItemName("Old Name");
        existing.setVehicleType(VehicleType.VAN);
        existing.setRequiredEmployeeCount(2);
        existing.setServiceItemCost(BigDecimal.valueOf(100));
        existing.setServiceItemType(ServiceItemType.SERVICE);
        existing.setEstimatedDuration(30);

        when(serviceItemRepository.findById(id)).thenReturn(Optional.of(existing));
        when(serviceItemRepository.save(any(ServiceItem.class))).thenAnswer(inv -> inv.getArgument(0));

        ServiceItemRequest request = new ServiceItemRequest(
                "New Name",
                "CAR",
                3,
                BigDecimal.valueOf(150),
                "MODIFICATION",
                45
        );

        ServiceItemResponse updated = adminService.updateService(id, request);

        assertEquals("New Name", updated.serviceItemName());
        assertEquals(VehicleType.CAR, updated.vehicleType());
        assertEquals(3, updated.requiredEmployeeCount());
        assertEquals(BigDecimal.valueOf(150), updated.serviceItemCost());
        assertEquals(ServiceItemType.MODIFICATION, updated.serviceItemType());
        assertEquals(45, updated.estimatedDuration());

        verify(serviceItemRepository).findById(id);
        verify(serviceItemRepository).save(any(ServiceItem.class));
    }

    @Test
    void testUpdateService_NotFound() {
        when(serviceItemRepository.findById(123L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> adminService.updateService(123L,
                new ServiceItemRequest("n","CAR",1, BigDecimal.TEN, "SERVICE", 10)));
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

    @Test
    void testCreateUser_Admin_Success() {
        UserCreateRequest req = new UserCreateRequest("alice", "pass123", "a@b.com", "ADMIN");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass123")).thenReturn("ENC(pass123)");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
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
        assertTrue(resp.requiresPasswordChange());

        verify(passwordEncoder).encode("pass123");
        verify(userRepository).save(captor.capture());
        User toSave = captor.getValue();
        assertTrue(toSave instanceof Admin);
        assertEquals("alice", toSave.getUsername());
        assertEquals("a@b.com", toSave.getEmail());
        assertEquals("ENC(pass123)", toSave.getPassword());
    }

    @Test
    void testCreateUser_InvalidRole_ThrowsBadRequest() {
        UserCreateRequest req = new UserCreateRequest("bob", "pw", "b@c.com", "MANAGER");
        assertThrows(BadRequestException.class, () -> adminService.createUser(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testCreateUser_BlankPassword_ThrowsBadRequest() {
        UserCreateRequest req = new UserCreateRequest("bob", "  ", "b@c.com", "ADMIN");
        assertThrows(BadRequestException.class, () -> adminService.createUser(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testCreateUser_ConflictUsername() {
        User existing = new User();
        existing.setId(99L);
        when(userRepository.findByUsername("dup")).thenReturn(Optional.of(existing));
        UserCreateRequest req = new UserCreateRequest("dup", "pw", "x@y.com", "EMPLOYEE");
        assertThrows(ResourceConflictException.class, () -> adminService.createUser(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testCreateUser_ConflictEmail() {
        when(userRepository.findByUsername("ok")).thenReturn(Optional.empty());
        User existing = new User(); existing.setId(100L);
        when(userRepository.findByEmail("dup@x.com")).thenReturn(Optional.of(existing));
        UserCreateRequest req = new UserCreateRequest("ok", "pw", "dup@x.com", "EMPLOYEE");
        assertThrows(ResourceConflictException.class, () -> adminService.createUser(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateUser_Success() {
        Admin existing = new Admin();
        existing.setId(5L);
        existing.setUsername("old");
        existing.setEmail("old@x.com");
        when(userRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(userRepository.findByUsername("new")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("new@x.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserUpdateRequest req = new UserUpdateRequest("new", "new@x.com", "IGNORED");
        var resp = adminService.updateUser(5L, req);

        assertEquals(5L, resp.id());
        assertEquals("new", resp.username());
        assertEquals("new@x.com", resp.email());
        assertEquals("ADMIN", resp.role());
        assertTrue(resp.requiresPasswordChange());

        verify(userRepository).save(any(User.class));
        assertEquals("new", existing.getUsername());
        assertEquals("new@x.com", existing.getEmail());
    }

    @Test
    void testUpdateUser_ConflictUsername() {
        Admin existing = new Admin(); existing.setId(5L);
        when(userRepository.findById(5L)).thenReturn(Optional.of(existing));
        User other = new User(); other.setId(7L);
        when(userRepository.findByUsername("taken")).thenReturn(Optional.of(other));
        UserUpdateRequest req = new UserUpdateRequest("taken", "free@x.com", null);
        assertThrows(ResourceConflictException.class, () -> adminService.updateUser(5L, req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateUser_ConflictEmail() {
        Admin existing = new Admin(); existing.setId(5L);
        when(userRepository.findById(5L)).thenReturn(Optional.of(existing));
        User other = new User(); other.setId(7L);
        when(userRepository.findByEmail("taken@x.com")).thenReturn(Optional.of(other));
        UserUpdateRequest req = new UserUpdateRequest("free", "taken@x.com", null);
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
    void testGetAllUsers_MapsRoles() {
        Admin a = new Admin(); a.setId(1L); a.setUsername("a"); a.setEmail("a@x.com");
        Employee e = new Employee(); e.setId(2L); e.setUsername("e"); e.setEmail("e@x.com");
        when(userRepository.findAll()).thenReturn(java.util.List.of(a, e));

        var list = adminService.getAllUsers();
        assertEquals(2, list.size());
        assertEquals("ADMIN", list.get(0).role());
        assertEquals("EMPLOYEE", list.get(1).role());
    }
}
