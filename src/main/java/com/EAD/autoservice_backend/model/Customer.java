package com.EAD.autoservice_backend.model;

import jakarta.persistence.*;

/**
 * Customer entity extending User with customer-specific attributes
 */
@Entity
@Table(name = "customers")
public class Customer extends User {

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;


    // Constructors
    public Customer() {
        super();
        setRole(Role.CUSTOMER);
    }

    public Customer(String username, String email, String password) {
        super(username, email, password);
        setRole(Role.CUSTOMER);
    }

    // Getters and Setters
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public Long getCustomerId() {
        return getId();
    }
}

