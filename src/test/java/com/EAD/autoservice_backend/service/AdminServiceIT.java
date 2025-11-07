package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.UserCreateRequest;
import com.EAD.autoservice_backend.dto.UserUpdateRequest;
import com.EAD.autoservice_backend.model.Admin;
import com.EAD.autoservice_backend.model.Employee;
import com.EAD.autoservice_backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdminServiceIT {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createAdminUser_thenUpdate_thenDelete() {
        var created = adminService.createUser(new UserCreateRequest(
                "admin_it",
                "strongPass1!",
                "admin_it@example.com",
                "ADMIN"
        ));
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
                null
        ));
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
                "EMPLOYEE"
        ));
        assertEquals("EMPLOYEE", created.role());
        var before = userRepository.findById(created.id()).orElseThrow();
        assertInstanceOf(Employee.class, before);

        // Change role => relies on native update statement; H2 is in PostgreSQL mode via test profile
        var afterRole = adminService.updateUser(created.id(), new UserUpdateRequest(null, null, "ADMIN"));
        assertEquals("ADMIN", afterRole.role());
        var reloaded = userRepository.findById(created.id()).orElseThrow();
        assertInstanceOf(Admin.class, reloaded);
    }
}
