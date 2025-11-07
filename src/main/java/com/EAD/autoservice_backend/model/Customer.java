package com.EAD.autoservice_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * Customer entity extending User with customer-specific attributes
 */
@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
public class Customer extends User {

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    public Customer(String username, String email, String password) {
        super(username, email, password);
        setRole(Role.CUSTOMER);
    }

    @Override
    public Role getRole() {
        if (super.getRole() == null) {
            setRole(Role.CUSTOMER);
        }
        return super.getRole();
    }

}

