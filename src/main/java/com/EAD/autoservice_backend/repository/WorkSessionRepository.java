package com.EAD.autoservice_backend.repository;

import com.EAD.autoservice_backend.model.WorkSession;
import com.EAD.autoservice_backend.model.SessionType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface WorkSessionRepository extends JpaRepository<WorkSession, Long> {
    Optional<WorkSession> findBySessionType(SessionType sessionType);

}


