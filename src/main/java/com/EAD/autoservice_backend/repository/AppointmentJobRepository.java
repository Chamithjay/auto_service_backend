package com.EAD.autoservice_backend.repository;

import com.EAD.autoservice_backend.model.Appointment;
import com.EAD.autoservice_backend.model.AppointmentJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentJobRepository extends JpaRepository<AppointmentJob, Long> {
    List<AppointmentJob> findByAppointment(Appointment appointment);


}
