package com.EAD.autoservice_backend.repository;

import com.EAD.autoservice_backend.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Find appointments by employee through JobAssignment
    @Query("SELECT DISTINCT a FROM Appointment a " +
            "JOIN AppointmentJob aj ON a.appointmentId = aj.appointment.appointmentId " +
            "JOIN JobAssignment ja ON aj.appointmentJobId = ja.appointmentJob.appointmentJobId " +
            "JOIN Employee e ON ja.employee.id = e.id " +
            "WHERE e.id = :employeeId " +
            "AND a.appointmentDate >= CURRENT_DATE " +
            "AND a.status IN (com.EAD.autoservice_backend.model.Status.NEW, " +
            "                 com.EAD.autoservice_backend.model.Status.ONGOING) " +
            "ORDER BY a.appointmentDate ASC, a.startTime ASC")
    List<Appointment> findUpcomingAppointmentsByEmployee(@Param("employeeId") Long employeeId);

    // Find today's appointments for employee
    @Query("SELECT DISTINCT a FROM Appointment a " +
            "JOIN AppointmentJob aj ON a.appointmentId = aj.appointment.appointmentId " +
            "JOIN JobAssignment ja ON aj.appointmentJobId = ja.appointmentJob.appointmentJobId " +
            "JOIN Employee e ON ja.employee.id = e.id " +
            "WHERE e.id = :employeeId " +
            "AND a.appointmentDate = CURRENT_DATE " +
            "ORDER BY a.startTime ASC")
    List<Appointment> findTodayAppointmentsByEmployee(@Param("employeeId") Long employeeId);
}