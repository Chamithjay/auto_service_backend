package com.EAD.autoservice_backend.repository;

import com.EAD.autoservice_backend.dto.reports.LeaveReport;
import com.EAD.autoservice_backend.model.Leave;
import com.EAD.autoservice_backend.model.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {

    List<Leave> findByLeaveStatusOrderByLeaveDateDesc(LeaveStatus status);

    List<Leave> findAllByOrderByLeaveDateDesc();

    List<Leave> findByEmployeeId(Long employeeId);

    List<Leave> findByLeaveStatus(LeaveStatus status);

    /**
     * Report Query: Leave Report
     * Gets the count of all leave requests, grouped by leave type,
     * within a specified date range.
     */
    @Query("SELECT new com.EAD.autoservice_backend.dto.reports.LeaveReport(l.leaveType, COUNT(l)) " +
            "FROM Leave l " +
            "WHERE l.leaveDate BETWEEN :startDate AND :endDate " +
            "GROUP BY l.leaveType")
    List<LeaveReport> getLeaveReport(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}