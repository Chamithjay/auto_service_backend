package com.EAD.autoservice_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Employee entity extending User with employee-specific attributes
 */
@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
public class Employee extends User {

    @Column(name = "employee_id", unique = true, length = 20)
    private String employeeId;

    @Column(name = "position", length = 50)
    private String position;

    @Column(name = "department", length = 50)
    private String department;

    @Column(name = "salary", precision = 10, scale = 2)
    private BigDecimal salary;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    public Employee(String username, String email, String password) {
        super(username, email, password);
        setRole(Role.EMPLOYEE);
    }

    @Override
    public Role getRole() {
        if (super.getRole() == null) {
            setRole(Role.EMPLOYEE);
        }
        return super.getRole();
    }
}

