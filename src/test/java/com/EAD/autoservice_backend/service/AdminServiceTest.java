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
        // This method runs before each test, setting up common objects.
        serviceItem = new ServiceItem();
        serviceItem.setServiceItemId(1L);
        serviceItem.setServiceItemName("Oil Change");
        serviceItem.setServiceItemCost(BigDecimal.valueOf(50.0));
    }

    /**
     * Test case for the "happy path" of getServiceById.
     * It verifies that the service returns the correct ServiceItem when a valid ID is provided.
     */
    @Test
    void testGetServiceById_Success() {
        // Arrange: Define the behavior of the mock repository.
        // When findById(1L) is called on the repository, return our sample serviceItem.
        when(serviceItemRepository.findById(1L)).thenReturn(Optional.of(serviceItem));

        // Act: Call the method we are testing.
        ServiceItem foundService = adminService.getServiceById(1L);

        // Assert: Verify the results.
        // Check that the returned service is not null and has the correct properties.
        assertNotNull(foundService);
        assertEquals("Oil Change", foundService.getServiceItemName());
        assertEquals(1L, foundService.getServiceItemId());

        // Verify that the findById method was called exactly once on the repository.
        verify(serviceItemRepository, times(1)).findById(1L);
    }

    /**
     * Test case for the "unhappy path" of getServiceById.
     * It verifies that the service throws a ResourceNotFoundException when an invalid ID is provided.
     */
    @Test
    void testGetServiceById_NotFound() {
        // Arrange: Define the mock behavior for a non-existent ID.
        // When findById is called with any Long, return an empty Optional.
        when(serviceItemRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert: Verify that executing the method throws the expected exception.
        // We use assertThrows to confirm that the correct exception type is thrown.
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            adminService.getServiceById(99L);
        });

        // Optionally, you can assert the exception message.
        assertEquals("ServiceItem not found with id: 99", exception.getMessage());

        // Verify that the findById method was called exactly once.
        verify(serviceItemRepository, times(1)).findById(99L);
    }

    /**
     * Test case for creating a new service item.
     */
    @Test
    void testCreateServiceItem() {
        // Arrange
        ServiceItemRequest request = new ServiceItemRequest(
                "Tire Rotation",
                "CAR",
                1,
                BigDecimal.valueOf(45.0),
                "SERVICE",
                60
        );

        // This will be the object returned by the save method.
        ServiceItem savedItem = new ServiceItem();
        savedItem.setServiceItemId(2L);
        savedItem.setServiceItemName("Tire Rotation");

        // When the repository's save method is called with any ServiceItem,
        // return our predefined savedItem.
        when(serviceItemRepository.save(any(ServiceItem.class))).thenReturn(savedItem);

        // Act
        ServiceItem createdItem = adminService.createServiceItem(request);

        // Assert
        assertNotNull(createdItem);
        assertEquals(2L, createdItem.getServiceItemId());
        assertEquals("Tire Rotation", createdItem.getServiceItemName());

        // Verify that save was called once.
        verify(serviceItemRepository, times(1)).save(any(ServiceItem.class));
    }

    @Test
    void testUpdateService_Success() {
        // Arrange
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

        // Act
        ServiceItem updated = adminService.updateService(id, request);

        // Assert
        assertEquals("New Name", updated.getServiceItemName());
        assertEquals(VehicleType.CAR, updated.getVehicleType());
        assertEquals(3, updated.getRequiredEmployeeCount());
        assertEquals(BigDecimal.valueOf(150), updated.getServiceItemCost());
        assertEquals(ServiceItemType.MODIFICATION, updated.getServiceItemType());
        assertEquals(45, updated.getEstimatedDuration());

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
        // Arrange
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

        // Act
        UserCreateResponse resp = adminService.createUser(req);

        // Assert
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
        // Existing Admin
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
        assertEquals("ADMIN", resp.role()); // role unchanged and mapped by instance
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
