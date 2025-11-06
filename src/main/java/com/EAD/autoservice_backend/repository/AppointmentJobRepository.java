package com.EAD.autoservice_backend.repository;

import com.EAD.autoservice_backend.dto.reports.ServicePopularity;
import com.EAD.autoservice_backend.model.AppointmentJob;
import com.EAD.autoservice_backend.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentJobRepository extends JpaRepository<AppointmentJob, Long> {

    /**
     * Report Query: Service Popularity
     * Gets the count of all booked services, grouped by service name,
     * within a specified date range.
     */
    @Query("SELECT new com.EAD.autoservice_backend.dto.reports.ServicePopularity(s.serviceItemName, COUNT(aj)) " +
            "FROM AppointmentJob aj JOIN aj.serviceItem s " +
            "JOIN aj.appointment a " + // Join with Appointment to filter by date
            "WHERE a.appointmentDate BETWEEN :startDate AND :endDate " +
            "GROUP BY s.serviceItemName")
    List<ServicePopularity> getServicePopularity(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT aj FROM AppointmentJob aj WHERE aj.appointment.appointmentId = :appointmentId")
    List<AppointmentJob> findByAppointmentId(@Param("appointmentId") Long appointmentId);

    @Query("SELECT COUNT(aj) FROM AppointmentJob aj WHERE aj.appointment.appointmentId = :appointmentId")
    Integer countByAppointmentId(@Param("appointmentId") Long appointmentId);

    @Query("SELECT COUNT(aj) FROM AppointmentJob aj WHERE aj.appointment.appointmentId = :appointmentId AND aj.jobStatus = :status")
    Integer countByAppointmentIdAndJobStatus(@Param("appointmentId") Long appointmentId, @Param("status") Status status);
}