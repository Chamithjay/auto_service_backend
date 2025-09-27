// File: src/main/java/com/EAD/autoservice_backend/model/AppointmentJob.java
package com.EAD.autoservice_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "appointment_jobs", // <-- FIXED
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_APPOINTMENT_JOB",
                        columnNames = {"appointment_id", "service_item_id"}
                )
        })
public class AppointmentJob {
    // ... (rest of the file is fine)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentJobId;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status jobStatus = Status.NEW;

    private String jobNote;

    @Column(precision = 17, scale = 2)
    private BigDecimal additional_cost;

    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "service_item_id", nullable = false)
    private ServiceItem serviceItem;

    @OneToMany(mappedBy = "appointmentJob")
    private Set<JobAssignment> employeeAssignments;
}