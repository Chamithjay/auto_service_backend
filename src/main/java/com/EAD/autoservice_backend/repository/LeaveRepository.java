package com.EAD.autoservice_backend.repository;

import com.EAD.autoservice_backend.model.Leave;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveRepository extends JpaRepository<Leave, Long> {
}

