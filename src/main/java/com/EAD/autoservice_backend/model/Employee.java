package com.EAD.autoservice_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;


@Getter
@Setter
@Entity
@DiscriminatorValue("EMPLOYEE")
public class Employee extends User{

    @Column(nullable = false, unique = true)
    private String nic;
}
