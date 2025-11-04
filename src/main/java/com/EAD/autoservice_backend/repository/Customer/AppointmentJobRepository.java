package com.EAD.autoservice_backend.repository;

import com.EAD.autoservice_backend.model.AppointmentJob;
import com.EAD.autoservice_backend.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for AppointmentJob entity
 */
@Repository
public interface AppointmentJobRepository extends JpaRepository<AppointmentJob, Long> {
    
    /**
     * Find all jobs for a specific appointment
     */
    @Query("SELECT aj FROM AppointmentJob aj WHERE aj.appointment.appointmentId = :appointmentId")
    List<AppointmentJob> findByAppointmentId(@Param("appointmentId") Long appointmentId);

    /**
     * Count total jobs for an appointment
     */
    @Query("SELECT COUNT(aj) FROM AppointmentJob aj WHERE aj.appointment.appointmentId = :appointmentId")
    Integer countByAppointmentId(@Param("appointmentId") Long appointmentId);

    /**
     * Count completed jobs for an appointment
     */
    @Query("SELECT COUNT(aj) FROM AppointmentJob aj WHERE aj.appointment.appointmentId = :appointmentId AND aj.jobStatus = :status")
    Integer countByAppointmentIdAndJobStatus(@Param("appointmentId") Long appointmentId, @Param("status") Status status);
}