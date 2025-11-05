package com.EAD.autoservice_backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Employee entity extending User with employee-specific attributes
 */
@Entity
@DiscriminatorValue("EMPLOYEE")
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

    // Constructors
    public Employee() {
        super();
        setRole(Role.EMPLOYEE);
    }

    public Employee(String username, String email, String password) {
        super(username, email, password);
        setRole(Role.EMPLOYEE);
    }

    // Getters and Setters
    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

