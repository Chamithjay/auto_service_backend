package com.EAD.autoservice_backend.repository;

import com.EAD.autoservice_backend.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByCustomerId(Long customerId);
    List<Appointment> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    @Query("""
    SELECT a FROM Appointment a
    WHERE a.customer.id = :customerId
      AND a.createdAt BETWEEN :startDate AND :endDate
    ORDER BY a.createdAt DESC
""")
    List<Appointment> findByCustomerIdAndDateRange(
            @Param("customerId") Long customerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );





}


