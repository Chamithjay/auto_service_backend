package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.ServiceItemRequest;
import com.EAD.autoservice_backend.dto.UserCreateRequest;
import com.EAD.autoservice_backend.dto.UserUpdateRequest;
import com.EAD.autoservice_backend.exception.ResourceConflictException;
import com.EAD.autoservice_backend.exception.ResourceNotFoundException;
import com.EAD.autoservice_backend.model.Admin;
import com.EAD.autoservice_backend.model.Employee;
import com.EAD.autoservice_backend.model.ServiceItemType;
import com.EAD.autoservice_backend.model.VehicleType;
import com.EAD.autoservice_backend.repository.ServiceItemRepository;
import com.EAD.autoservice_backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdminServiceIT {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceItemRepository serviceItemRepository;

    @Test
    void createAdminUser_thenUpdate_thenDelete() {
        var created = adminService.createUser(new UserCreateRequest(
                "admin_it",
                "strongPass1!",
                "admin_it@example.com",
                "ADMIN"));
        assertNotNull(created.id());
        assertEquals("ADMIN", created.role());
        assertTrue(created.requiresPasswordChange());

        // Verify persisted subtype
        var persisted = userRepository.findById(created.id()).orElseThrow();
        assertInstanceOf(Admin.class, persisted);

        // Update username and email only
        var updated = adminService.updateUser(created.id(), new UserUpdateRequest(
                "admin_it_renamed",
                "admin_it2@example.com",
                null));
        assertEquals("admin_it_renamed", updated.username());
        assertEquals("admin_it2@example.com", updated.email());
        assertEquals("ADMIN", updated.role());

        // Delete
        adminService.deleteUser(created.id());
        assertFalse(userRepository.existsById(created.id()));
    }

    @Test
    void createEmployeeUser_andChangeRoleToAdmin() {
        var created = adminService.createUser(new UserCreateRequest(
                "emp_it",
                "pw2!",
                "emp_it@example.com",
                "EMPLOYEE"));
        assertEquals("EMPLOYEE", created.role());
        var before = userRepository.findById(created.id()).orElseThrow();
        assertInstanceOf(Employee.class, before);

        // Change role => relies on native update statement; H2 is in PostgreSQL mode
        // via test profile
        var afterRole = adminService.updateUser(created.id(), new UserUpdateRequest(null, null, "ADMIN"));
        assertEquals("ADMIN", afterRole.role());
        var reloaded = userRepository.findById(created.id()).orElseThrow();
        assertInstanceOf(Admin.class, reloaded);
    }

    @Test
    void createServiceItem_persistence_success() {
        ServiceItemRequest req = new ServiceItemRequest(
                "Full Service",
                "CAR",
                2,
                BigDecimal.valueOf(199.99),
                "SERVICE",
                120);

        var created = adminService.createServiceItem(req);

        assertNotNull(created.serviceItemId());
        assertEquals("Full Service", created.serviceItemName());
        assertEquals(VehicleType.CAR, created.vehicleType());
        assertEquals(ServiceItemType.SERVICE, created.serviceItemType());
        assertEquals(2, created.requiredEmployeeCount());
        assertEquals(BigDecimal.valueOf(199.99), created.serviceItemCost());
        assertEquals(120, created.estimatedDuration());

        // Verify persistence
        var persisted = serviceItemRepository.findById(created.serviceItemId()).orElseThrow();
        assertEquals("Full Service", persisted.getServiceItemName());
    }

    @Test
    void updateServiceItem_modifiesAllFields() {
        // Create initial service
        ServiceItemRequest initial = new ServiceItemRequest(
                "Original",
                "VAN",
                1,
                BigDecimal.valueOf(50),
                "SERVICE",
                30);
        var created = adminService.createServiceItem(initial);

        // Update with new values
        ServiceItemRequest updated = new ServiceItemRequest(
                "Updated",
                "BUS",
                3,
                BigDecimal.valueOf(250),
                "MODIFICATION",
                180);
        var result = adminService.updateService(created.serviceItemId(), updated);

        assertEquals("Updated", result.serviceItemName());
        assertEquals(3, result.requiredEmployeeCount());
        assertEquals(BigDecimal.valueOf(250), result.serviceItemCost());
        assertEquals(ServiceItemType.MODIFICATION, result.serviceItemType());
        assertEquals(180, result.estimatedDuration());
    }

    @Test
    void deleteServiceItem_removesFromDatabase() {
        ServiceItemRequest req = new ServiceItemRequest(
                "To Delete",
                "MOTORCYCLE",
                1,
                BigDecimal.TEN,
                "SERVICE",
                20);
        var created = adminService.createServiceItem(req);

        adminService.deleteService(created.serviceItemId());

        assertFalse(serviceItemRepository.existsById(created.serviceItemId()));
    }

    @Test
    void createUser_duplicateUsername_throws() {
        UserCreateRequest req1 = new UserCreateRequest(
                "duplicate",
                "pass1",
                "email1@x.com",
                "ADMIN");
        adminService.createUser(req1);

        UserCreateRequest req2 = new UserCreateRequest(
                "duplicate",
                "pass2",
                "email2@x.com",
                "EMPLOYEE");

        assertThrows(ResourceConflictException.class, () -> adminService.createUser(req2));
    }

    @Test
    void createUser_duplicateEmail_throws() {
        UserCreateRequest req1 = new UserCreateRequest(
                "user1",
                "pass1",
                "same@x.com",
                "ADMIN");
        adminService.createUser(req1);

        UserCreateRequest req2 = new UserCreateRequest(
                "user2",
                "pass2",
                "same@x.com",
                "EMPLOYEE");

        assertThrows(ResourceConflictException.class, () -> adminService.createUser(req2));
    }

    @Test
    void updateUser_toSameEmail_allowed() {
        var created = adminService.createUser(new UserCreateRequest(
                "testuser",
                "pass",
                "test@x.com",
                "ADMIN"));

        var updated = adminService.updateUser(created.id(), new UserUpdateRequest(
                "testuser",
                "test@x.com",
                null));

        assertEquals("test@x.com", updated.email());
    }

    @Test
    void updateUser_changeEmail_thenDelete() {
        var created = adminService.createUser(new UserCreateRequest(
                "user_delete",
                "pass",
                "original@x.com",
                "EMPLOYEE"));

        var updated = adminService.updateUser(created.id(), new UserUpdateRequest(
                null,
                "updated@x.com",
                null));

        assertEquals("updated@x.com", updated.email());

        adminService.deleteUser(created.id());
        assertFalse(userRepository.existsById(created.id()));
    }

    @Test
    void getServiceById_returnsCorrectData() {
        ServiceItemRequest req = new ServiceItemRequest(
                "Retrieve Test",
                "CAR",
                1,
                BigDecimal.valueOf(75.50),
                "SERVICE",
                60);
        var created = adminService.createServiceItem(req);

        var retrieved = adminService.getServiceById(created.serviceItemId());

        assertEquals("Retrieve Test", retrieved.serviceItemName());
        assertEquals(BigDecimal.valueOf(75.50), retrieved.serviceItemCost());
    }

    @Test
    void getServiceById_notFound_throws() {
        assertThrows(ResourceNotFoundException.class, () -> adminService.getServiceById(99999L));
    }

    @Test
    void getUserById_returnsCorrectRole() {
        var admin = adminService.createUser(new UserCreateRequest(
                "admin_retrieve",
                "pass",
                "admin@x.com",
                "ADMIN"));

        var retrieved = adminService.getUserById(admin.id());

        assertEquals("ADMIN", retrieved.role());
        assertEquals("admin_retrieve", retrieved.username());
    }

    @Test
    void getAllServices_returnsMultiple() {
        ServiceItemRequest req1 = new ServiceItemRequest("Service1", "CAR", 1, BigDecimal.TEN, "SERVICE", 30);
        ServiceItemRequest req2 = new ServiceItemRequest("Service2", "VAN", 2, BigDecimal.TEN, "MODIFICATION", 60);

        adminService.createServiceItem(req1);
        adminService.createServiceItem(req2);

        var allServices = adminService.getAllServices();

        assertTrue(allServices.size() >= 2);
    }

    @Test
    void getAllUsers_returnsMultiple() {
        adminService.createUser(new UserCreateRequest("user1", "pass", "u1@x.com", "ADMIN"));
        adminService.createUser(new UserCreateRequest("user2", "pass", "u2@x.com", "EMPLOYEE"));

        var allUsers = adminService.getAllUsers();

        assertTrue(allUsers.size() >= 2);
    }
}
