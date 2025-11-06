package com.EAD.autoservice_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Admin entity extending User with admin-specific attributes
 */
@Entity
@Table(name = "admins")
public class Admin extends User {

    @Column(name = "department", length = 50)
    private String department;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "access_level")
    private Integer accessLevel = 1; // 1 = full access, 2 = limited, etc.

    // Constructors
    public Admin() {
        super();
        setRole(Role.ADMIN);
    }

    public Admin(String username, String email, String password) {
        super(username, email, password);
        setRole(Role.ADMIN);
    }

    // Getters and Setters
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Integer getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(Integer accessLevel) {
        this.accessLevel = accessLevel;
    }
}

