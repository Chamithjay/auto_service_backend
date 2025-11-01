package com.EAD.autoservice_backend.model;

import jakarta.persistence.*;
import lombok.*;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId;

    @Column(name = "appointment_date", nullable = false) // 2025-10-24
    private LocalDate appointmentDate;

    @Column(name = "appointment_start_time", nullable = false) //18:50:00
    private LocalTime startTime;

    @Column(name = "appointment_end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.NEW;

    @Column(precision = 17, scale = 2)
    private BigDecimal totalCost;

    @ManyToOne
    @JoinColumn(name = "vehicl_id", nullable = false)
    private Vehicle vehicle;
}
