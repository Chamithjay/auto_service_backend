package com.EAD.autoservice_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Admin entity extending User with admin-specific attributes
 */
@Entity
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor
public class Admin extends User {

    @Column(name = "department", length = 50)
    private String department;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "access_level")
    private Integer accessLevel = 1; // 1 = full access, 2 = limited, etc.

    public Admin(String username, String email, String password) {
        super(username, email, password);
        setRole(Role.ADMIN);
    }

    @Override
    public Role getRole() {
        if (super.getRole() == null) {
            setRole(Role.ADMIN);
        }
        return super.getRole();
    }
}
