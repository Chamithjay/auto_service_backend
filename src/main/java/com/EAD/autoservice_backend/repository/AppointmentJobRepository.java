package com.EAD.autoservice_backend.repository;

import com.EAD.autoservice_backend.dto.ServicePopularity;
import com.EAD.autoservice_backend.model.Appointment;
import com.EAD.autoservice_backend.model.AppointmentJob;
import com.EAD.autoservice_backend.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentJobRepository extends JpaRepository<AppointmentJob, Long> {

    /**
     * Derived query: Find all jobs for a given appointment
     */
    List<AppointmentJob> findByAppointment(Appointment appointment);

    /**
     * Report Query: Service Popularity
     * Gets the count of all booked services, grouped by service name,
     * within a specified date range.
     */
    @Query("SELECT new com.EAD.autoservice_backend.dto.ServicePopularity(s.serviceItemName, COUNT(aj)) " +
            "FROM AppointmentJob aj JOIN aj.serviceItem s " +
            "JOIN aj.appointment a " +
            "WHERE a.appointmentDate BETWEEN :startDate AND :endDate " +
            "GROUP BY s.serviceItemName")
    List<ServicePopularity> getServicePopularity(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find jobs by appointment ID
     */
    @Query("SELECT aj FROM AppointmentJob aj WHERE aj.appointment.appointmentId = :appointmentId")
    List<AppointmentJob> findByAppointmentId(@Param("appointmentId") Long appointmentId);

    /**
     * Count all jobs by appointment ID
     */
    @Query("SELECT COUNT(aj) FROM AppointmentJob aj WHERE aj.appointment.appointmentId = :appointmentId")
    Integer countByAppointmentId(@Param("appointmentId") Long appointmentId);

    /**
     * Count jobs by appointment ID and job status
     */
    @Query("SELECT COUNT(aj) FROM AppointmentJob aj WHERE aj.appointment.appointmentId = :appointmentId AND aj.itemStatus = :status")
    Integer countByAppointmentIdAndJobStatus(@Param("appointmentId") Long appointmentId, @Param("status") Status status);
}
