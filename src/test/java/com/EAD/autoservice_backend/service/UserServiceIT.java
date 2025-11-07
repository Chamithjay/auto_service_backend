package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.model.Admin;
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
}

