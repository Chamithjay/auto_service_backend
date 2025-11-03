package com.EAD.autoservice_backend.repository;

import com.EAD.autoservice_backend.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}
