package com.EAD.autoservice_backend.repository;

import com.EAD.autoservice_backend.model.Leave;
import com.EAD.autoservice_backend.model.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface LeaveRepository extends JpaRepository<Leave, Long> {

    @Query("""
        SELECT COUNT(l)
        FROM Leave l
        WHERE l.leaveDate = :date
          AND (l.leaveType = :leaveType OR l.leaveType = com.EAD.autoservice_backend.model.LeaveType.FULLDAY)
    """)
    long countEmployeesOnLeave(
            @Param("date") LocalDate date,
            @Param("leaveType") LeaveType leaveType
    );
}
