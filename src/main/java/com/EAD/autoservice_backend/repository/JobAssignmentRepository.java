package com.EAD.autoservice_backend.repository;

import com.EAD.autoservice_backend.model.JobAssignment;
import com.EAD.autoservice_backend.model.SessionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface JobAssignmentRepository extends JpaRepository<JobAssignment, Long> {

    @Query("""
    SELECT COALESCE(SUM(si.estimatedDuration), 0)
    FROM JobAssignment ja
    JOIN ja.appointmentJob aj
    JOIN aj.appointment a
    JOIN aj.serviceItem si
    WHERE ja.employee.id = :employeeId
    AND a.appointmentDate = :date
    AND a.sessionType = :sessionType
""")
    int sumTotalDurationByDateAndEmployeeAndSession(
            @Param("employeeId") Long employeeId,
            @Param("date") LocalDate date,
            @Param("sessionType") SessionType sessionType
    );

    @Query("SELECT ja.employee.id FROM JobAssignment ja ORDER BY ja.id DESC LIMIT 1")
    Optional<Long> findLastAssignedEmployeeId();

}
