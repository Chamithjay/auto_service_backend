package com.EAD.autoservice_backend.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

@Entity
@Table(name = "work_sessions")
@Getter
@Setter
public class WorkSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private SessionType sessionType;
    private LocalTime startTime;
    private LocalTime endTime;
    private double durationHours;
}
