package com.EAD.autoservice_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Table(
        name = "appointment_job",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_APPOINTMENT_JOB",
                        columnNames = {"appointment_id", "service_item_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"appointment", "serviceItem", "employeeAssignments"})
@EqualsAndHashCode(exclude = {"appointment", "serviceItem", "employeeAssignments"})
public class AppointmentJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus itemStatus = AppointmentStatus.NEW;

    private String jobNote;

    @Column(precision = 17, scale = 2)
    private BigDecimal additionalCost;

    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "service_item_id", nullable = false)
    private ServiceItem serviceItem;

    @OneToMany(mappedBy = "appointmentJob")
    private Set<JobAssignment> employeeAssignments;
}
