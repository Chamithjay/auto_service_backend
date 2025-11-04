package com.EAD.autoservice_backend.repository;

import com.EAD.autoservice_backend.model.Appointment;
import com.EAD.autoservice_backend.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT COALESCE(SUM(a.totalCost), 0) FROM Appointment a")
    BigDecimal getTotalRevenue();

    @Query("SELECT a FROM Appointment a WHERE a.status = :status ORDER BY a.appointmentDate DESC")
    List<Appointment> findByStatusOrderByDateDesc(@Param("status") Status status);

    @Query("SELECT a FROM Appointment a ORDER BY a.appointmentDate DESC")
    List<Appointment> findAllOrderByDateDesc();

    @Query("SELECT a FROM Appointment a WHERE YEAR(a.appointmentDate) = YEAR(CURRENT_DATE) ORDER BY a.appointmentDate")
    List<Appointment> findAllByCurrentYear();
}
