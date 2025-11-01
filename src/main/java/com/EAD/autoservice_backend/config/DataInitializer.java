package com.EAD.autoservice_backend.config;

import com.EAD.autoservice_backend.model.Admin;
import com.EAD.autoservice_backend.model.Role;
import com.EAD.autoservice_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Initializes database with default admin user
 * This runs once when the application starts
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if admin already exists
        if (!userRepository.existsByRole(Role.ADMIN)) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setEmail("admin@autoservice.com");
            admin.setPassword(passwordEncoder.encode("adminpwd"));
            admin.setRole(Role.ADMIN);
            admin.setDepartment("Management");
            admin.setAccessLevel(1);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());

            userRepository.save(admin);
            System.out.println("✅ Default admin user created!");
            System.out.println("   Username: admin");
            System.out.println("   Password: adminpwd");
            System.out.println("   Email: admin@autoservice.com");
        }
    }
}