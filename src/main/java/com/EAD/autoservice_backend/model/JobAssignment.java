package com.EAD.autoservice_backend.model;

import jakarta.persistence.*;
import lombok.*;


import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "job_assignments")
@Getter
@Setter
public class JobAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "appointment_job_id", nullable = false)
    private AppointmentJob appointmentJob;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(precision = 17, scale = 2)
    private BigDecimal additional_cost;

    @Column
    private String costNote;
}
