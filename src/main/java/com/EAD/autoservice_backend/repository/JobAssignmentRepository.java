package com.EAD.autoservice_backend.repository;

import com.EAD.autoservice_backend.dto.reports.EmployeePerformance;
import com.EAD.autoservice_backend.model.JobAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface JobAssignmentRepository extends JpaRepository<JobAssignment, Integer> {

    /**
     * Report Query: Employee Performance
     * This is a native PostgreSQL query.
     * 1. It joins 4 tables (using their quoted names) to connect assignments to their dates.
     * 2. It uses SUM(EXTRACT(EPOCH FROM ... ) / 3600.0) to calculate the
     * total hours as a decimal (double) value.
     * 3. This (String, Long, Double) result perfectly matches the EmployeePerformance DTO constructor.
     */
    @Query(value = "SELECT e.username AS employeeName, " +
            "COUNT(ja.job_assignment_id) AS totalJobs, " +
            "SUM(EXTRACT(EPOCH FROM (ja.end_time - ja.start_time)) / 3600.0) AS totalHours " +
            "FROM \"job assignments\" ja " +  // <-- Corrected table name
            "JOIN users e ON ja.employee_id = e.id " +
            "JOIN \"appointment jobs\" aj ON ja.appointment_job_id = aj.appointment_job_id " + // <-- Corrected table name
            "JOIN appointments a ON aj.appointment_id = a.appointment_id " +
            "WHERE a.appointment_date BETWEEN :startDate AND :endDate " +
            "GROUP BY e.username",
            nativeQuery = true)
    List<EmployeePerformance> getEmployeePerformance(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}