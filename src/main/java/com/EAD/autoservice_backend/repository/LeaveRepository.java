package com.EAD.autoservice_backend.repository;

import com.EAD.autoservice_backend.dto.LeaveReqDTO;
import com.EAD.autoservice_backend.model.Leave;
import com.EAD.autoservice_backend.model.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {
    List<Leave> findByLeaveStatusOrderByLeaveDateDesc(LeaveStatus status);
    List<Leave> findAllByOrderByLeaveDateDesc();
//    @Query("SELECT new com.EAD.autoservice_backend.dto.LeaveReqDTO(" +
//            "l.leaveId, l.leaveType, l.leaveDate, l.leaveReason, l.approvedTime, l.approvedDate, l.leaveStatus, " +
//            "CAST(e.id AS string), e.username, NULL) " +  // employeeId, username, adminName
//            "FROM Leave l JOIN l.employee e " +
//            "ORDER BY l.leaveDate DESC")
//    List<LeaveReqDTO> findAllLeaveRequestsWithEmployee();
}
