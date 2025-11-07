package com.EAD.autoservice_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "leaves")
public class Leave {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long leaveId;

    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;

    @Column(nullable = false) // 2025-10-24
    private LocalDate leaveDate;

    private String leaveReason;

    private LocalTime approvedTime;

    private LocalDate approvedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveStatus leaveStatus = LeaveStatus.NEW;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private Admin admin;

}