package com.EAD.autoservice_backend.repository;

import com.EAD.autoservice_backend.dto.LeaveReport;
import com.EAD.autoservice_backend.model.Leave;
import com.EAD.autoservice_backend.model.LeaveStatus;
import com.EAD.autoservice_backend.model.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {

    /**
     * Check if an employee is on approved leave for a specific date and type
     */
    @Query("""
        SELECT COUNT(l) > 0
        FROM Leave l
        WHERE l.employee.id = :employeeId
          AND l.leaveDate = :date
          AND l.leaveStatus = 'APPROVED'
          AND (l.leaveType = :leaveType OR l.leaveType = 'FULLDAY')
    """)
    boolean isEmployeeOnApprovedLeave(@Param("employeeId") Long employeeId,
                                      @Param("date") LocalDate date,
                                      @Param("leaveType") LeaveType leaveType);

    // Find leaves by status, ordered descending by date
    List<Leave> findByLeaveStatusOrderByLeaveDateDesc(LeaveStatus status);

    // Get all leaves ordered descending by date
    List<Leave> findAllByOrderByLeaveDateDesc();

    // Find leaves by employee
    List<Leave> findByEmployeeId(Long employeeId);

    // Find leaves by status
    List<Leave> findByLeaveStatus(LeaveStatus status);

    /**
     * Report Query: Leave Report
     * Gets the count of leave requests grouped by leave type within a date range
     */
    @Query("SELECT new com.EAD.autoservice_backend.dto.LeaveReport(l.leaveType, COUNT(l)) " +
            "FROM Leave l " +
            "WHERE l.leaveDate BETWEEN :startDate AND :endDate " +
            "GROUP BY l.leaveType")
    List<LeaveReport> getLeaveReport(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
