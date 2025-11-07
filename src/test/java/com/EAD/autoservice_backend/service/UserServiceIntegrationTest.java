package com.EAD.autoservice_backend.service;

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
class UserServiceIT {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void forceResetPassword_updatesPassword_andClearsFlag() {
        Admin admin = new Admin();
        admin.setUsername("adminX");
        admin.setEmail("adminx@company.com");
        admin.setPassword("OLD");
        admin.setRequiresPasswordChange(true);
        admin = userRepository.save(admin);

        userService.forceResetPassword(admin.getId(), "newSecret!");

        var reloaded = userRepository.findById(admin.getId()).orElseThrow();
        assertNotEquals("OLD", reloaded.getPassword());
        assertFalse(reloaded.isRequiresPasswordChange());
    }

    @Test
    void forceResetPassword_withEmployee_success() {
        Employee emp = new Employee();
        emp.setUsername("empX");
        emp.setEmail("empx@company.com");
        emp.setPassword("INITIAL");
        emp.setRequiresPasswordChange(true);
        emp = userRepository.save(emp);

        userService.forceResetPassword(emp.getId(), "employeeNewPass");

        var reloaded = userRepository.findById(emp.getId()).orElseThrow();
        assertNotEquals("INITIAL", reloaded.getPassword());
        assertFalse(reloaded.isRequiresPasswordChange());
    }

    @Test
    void forceResetPassword_multipleResets_lastOneWins() {
        Admin admin = new Admin();
        admin.setUsername("resetTester");
        admin.setEmail("reset@company.com");
        admin.setPassword("ORIGINAL");
        admin.setRequiresPasswordChange(true);
        admin = userRepository.save(admin);

        // First reset
        userService.forceResetPassword(admin.getId(), "firstReset");
        var afterFirst = userRepository.findById(admin.getId()).orElseThrow();
        String firstPassword = afterFirst.getPassword();
        assertFalse(afterFirst.isRequiresPasswordChange());

        // Second reset
        userService.forceResetPassword(admin.getId(), "secondReset");
        var afterSecond = userRepository.findById(admin.getId()).orElseThrow();
        String secondPassword = afterSecond.getPassword();

        assertNotEquals(firstPassword, secondPassword);
        assertNotEquals("ORIGINAL", secondPassword);
        assertFalse(afterSecond.isRequiresPasswordChange());
    }

    @Test
    void forceResetPassword_complexPasswordPersisted() {
        Admin admin = new Admin();
        admin.setUsername("complexPass");
        admin.setEmail("complex@company.com");
        admin.setPassword("OLD");
        admin.setRequiresPasswordChange(true);
        admin = userRepository.save(admin);

        String complexPassword = "P@ssw0rd!#$%^&*()_+-=[]{}|;:',.<>?/`~";
        userService.forceResetPassword(admin.getId(), complexPassword);

        var reloaded = userRepository.findById(admin.getId()).orElseThrow();
        // Password should be encoded, not stored as-is
        assertNotEquals(complexPassword, reloaded.getPassword());
        assertFalse(reloaded.isRequiresPasswordChange());
    }

    @Test
    void forceResetPassword_flagAlwaysCleared() {
        Admin admin = new Admin();
        admin.setUsername("flagTest");
        admin.setEmail("flag@company.com");
        admin.setPassword("OLD");
        admin.setRequiresPasswordChange(true);
        admin = userRepository.save(admin);

        assertTrue(admin.isRequiresPasswordChange());
        userService.forceResetPassword(admin.getId(), "newPass");

        var reloaded = userRepository.findById(admin.getId()).orElseThrow();
        assertFalse(reloaded.isRequiresPasswordChange());
    }

    @Test
    void forceResetPassword_userNotFound_exceptionThrown() {
        assertThrows(Exception.class, () -> userService.forceResetPassword(99999L, "pass"));
    }
}
