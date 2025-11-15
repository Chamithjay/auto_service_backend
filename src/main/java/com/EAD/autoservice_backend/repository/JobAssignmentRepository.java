package com.EAD.autoservice_backend.repository;

import com.EAD.autoservice_backend.dto.EmployeePerformance;
import com.EAD.autoservice_backend.model.JobAssignment;
import com.EAD.autoservice_backend.model.SessionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobAssignmentRepository extends JpaRepository<JobAssignment, Long> {

    /**
     * Report Query: Employee Performance
     * Native PostgresSQL query for calculating total jobs and hours per employee
     */
    @Query(value = "SELECT e.username AS employeeName, " +
            "COUNT(ja.job_assignment_id) AS totalJobs, " +
            "SUM(EXTRACT(EPOCH FROM (ja.end_time - ja.start_time)) / 3600.0) AS totalHours " +
            "FROM \"job assignments\" ja " +
            "JOIN users e ON ja.employee_id = e.id " +
            "JOIN \"appointment jobs\" aj ON ja.appointment_job_id = aj.appointment_job_id " +
            "JOIN appointments a ON aj.appointment_id = a.appointment_id " +
            "WHERE a.appointment_date BETWEEN :startDate AND :endDate " +
            "GROUP BY e.username",
            nativeQuery = true)
    List<EmployeePerformance> getEmployeePerformance(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Sum total duration of services for a given employee on a date and session
     */
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

    /**
     * Get the last assigned employee ID
     */
    @Query("SELECT ja.employee.id FROM JobAssignment ja ORDER BY ja.id DESC LIMIT 1")
    Optional<Long> findLastAssignedEmployeeId();

    /**
     * Find assignments by appointment job ID
     */
    List<JobAssignment> findByAppointmentJobId(Long appointmentJobId);

    List<JobAssignment> findByAppointmentJob_Id(Long appointmentJobId);

    /**
     * Fetch today's assignments for a given employee
     */
    List<JobAssignment> findByEmployee_IdAndAppointmentJob_Appointment_AppointmentDate(Long employeeId, LocalDate date);

    /**
     * Fetch upcoming assignments (after today) for a given employee
     */
    List<JobAssignment> findByEmployee_IdAndAppointmentJob_Appointment_AppointmentDateAfter(Long employeeId, LocalDate date);
}
