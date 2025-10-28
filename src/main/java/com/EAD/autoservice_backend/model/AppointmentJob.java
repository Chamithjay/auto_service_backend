package com.EAD.autoservice_backend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Table(name = "appointment_job",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_APPOINTMENT_JOB",
                        columnNames = {"appointment_id", "service_item_id"} // The columns that must be unique together
                )
        })
public class AppointmentJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time") //18:50:00
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus itemStatus = AppointmentStatus.NEW;

    private String description;

    @Column(precision = 17, scale = 2)
    private BigDecimal additional_cost;

    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "service_itme_id", nullable = false)
    private ServiceItem serviceItem;

    @OneToMany(mappedBy = "appointmentJob")
    private Set<JobAssignment> employeeAssignments;
}
