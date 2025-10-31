package com.EAD.autoservice_backend.model;

import jakarta.persistence.*;

/**
 * Customer entity extending User with customer-specific attributes
 */
@Entity
@DiscriminatorValue("CUSTOMER")
public class Customer extends User {

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "loyalty_points")
    private Integer loyaltyPoints = 0;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }
}

