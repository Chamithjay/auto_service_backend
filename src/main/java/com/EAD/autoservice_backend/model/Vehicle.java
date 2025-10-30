package com.EAD.autoservice_backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vehicleId;

    @Column(nullable = false, length = 255)
    private String vehicleName;

    @Column(nullable = false, length = 255)
    private String registrationNo;

    @Column(nullable = false, length = 255)
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    @Column(length = 255)
    private String model;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "vehicle")
    private Set<Appointment> appointments;

    //Should add the customer.
}
