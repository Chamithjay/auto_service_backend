package com.EAD.autoservice_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "appointment_job_id", nullable = false)
    private AppointmentJob appointmentJob;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "start_time") //18:50:00
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;
}
