package com.EAD.autoservice_backend.repository;

import com.EAD.autoservice_backend.model.Appointment;
import com.EAD.autoservice_backend.model.SessionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;


@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("SELECT COALESCE(SUM(a.totalApproximatedDuration), 0) " +
            "FROM Appointment a " +
            "WHERE a.appointmentDate = :date " +
            "AND a.sessionType = :sessionType")
    long sumTotalDurationByDateAndSession(
            @Param("date") LocalDate date,
            @Param("sessionType") SessionType sessionType
    );

}


