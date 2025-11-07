package com.EAD.autoservice_backend.repository;

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


    List<Leave> findByLeaveStatusOrderByLeaveDateDesc(LeaveStatus status);
    List<Leave> findAllByOrderByLeaveDateDesc();
}
