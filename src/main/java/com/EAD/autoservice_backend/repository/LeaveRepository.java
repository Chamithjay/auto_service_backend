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

}
