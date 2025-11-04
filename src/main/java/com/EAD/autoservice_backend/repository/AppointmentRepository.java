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
import java.util.Optional;

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

    /**
     * Find all appointments for a specific customer
     */
    @Query("SELECT a FROM Appointment a WHERE a.vehicle.customer.id = :customerId ORDER BY a.appointmentDate DESC, a.startTime DESC")
    List<Appointment> findByCustomerId(@Param("customerId") Long customerId);

    /**
     * Find appointment by ID and customer ID (for authorization)
     */
    @Query("SELECT a FROM Appointment a WHERE a.appointmentId = :appointmentId AND a.vehicle.customer.id = :customerId")
    Optional<Appointment> findByAppointmentIdAndCustomerId(@Param("appointmentId") Long appointmentId, 
                                                            @Param("customerId") Long customerId);

    /**
     * Count total appointments by customer ID
     */
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.vehicle.customer.id = :customerId")
    Integer countByCustomerId(@Param("customerId") Long customerId);

    /**
     * Count active appointments (NEW or ONGOING) by customer ID
     */
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.vehicle.customer.id = :customerId AND a.status IN :statuses")
    Integer countByCustomerIdAndStatusIn(@Param("customerId") Long customerId, @Param("statuses") List<Status> statuses);

    /**
     * Count completed appointments by customer ID
     */
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.vehicle.customer.id = :customerId AND a.status = :status")
    Integer countByCustomerIdAndStatus(@Param("customerId") Long customerId, @Param("status") Status status);

    /**
     * Find active appointments for a customer (NEW or ONGOING)
     */
    @Query("SELECT a FROM Appointment a WHERE a.vehicle.customer.id = :customerId AND a.status IN :statuses ORDER BY a.appointmentDate DESC")
    List<Appointment> findActiveAppointmentsByCustomerId(@Param("customerId") Long customerId, @Param("statuses") List<Status> statuses);
}
