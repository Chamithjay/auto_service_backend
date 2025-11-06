package com.EAD.autoservice_backend.repository;

import com.EAD.autoservice_backend.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface JobAssignmentRepository extends JpaRepository<JobAssignment, Long> {

    @Query("SELECT ja FROM JobAssignment ja " +
            "JOIN FETCH ja.appointmentJob aj " +
            "JOIN FETCH aj.appointment a " +
            "JOIN FETCH aj.serviceItem si " +
            "WHERE ja.employee.id = :employeeId " +
            "AND a.appointmentDate >= CURRENT_DATE " +
            "ORDER BY a.appointmentDate ASC, a.startTime ASC")
    List<JobAssignment> findUpcomingAssignmentsByEmployee(@Param("employeeId") Long employeeId);

    @Query("SELECT ja FROM JobAssignment ja " +
            "JOIN FETCH ja.appointmentJob aj " +
            "JOIN FETCH aj.appointment a " +
            "JOIN FETCH aj.serviceItem si " +
            "WHERE ja.employee.id = :employeeId " +
            "AND a.appointmentDate = CURRENT_DATE " +
            "ORDER BY a.startTime ASC")
    List<JobAssignment> findTodayAssignmentsByEmployee(@Param("employeeId") Long employeeId);
}