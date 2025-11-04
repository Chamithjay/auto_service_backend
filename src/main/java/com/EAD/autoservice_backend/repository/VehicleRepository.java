package com.EAD.autoservice_backend.repository;

import com.EAD.autoservice_backend.model.Vehicle;
import com.EAD.autoservice_backend.model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("SELECT v.vehicleType, COUNT(v) FROM Vehicle v GROUP BY v.vehicleType")
    List<Object[]> countByVehicleType();

    List<Vehicle> findAllByOrderByCreatedAtDesc();

    /**
     * Find all vehicles by customer ID
     */
    @Query("SELECT v FROM Vehicle v WHERE v.customer.id = :customerId ORDER BY v.createdAt DESC")
    List<Vehicle> findByCustomerId(@Param("customerId") Long customerId);

    /**
     * Find vehicle by ID and customer ID (for authorization)
     */
    @Query("SELECT v FROM Vehicle v WHERE v.vehicleId = :vehicleId AND v.customer.id = :customerId")
    Optional<Vehicle> findByVehicleIdAndCustomerId(@Param("vehicleId") Long vehicleId, 
                                                    @Param("customerId") Long customerId);

    /**
     * Count vehicles by customer ID
     */
    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.customer.id = :customerId")
    Integer countByCustomerId(@Param("customerId") Long customerId);

    /**
     * Check if registration number exists for a customer
     */
    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM Vehicle v " +
           "WHERE v.registrationNo = :registrationNo AND v.customer.id = :customerId")
    boolean existsByRegistrationNoAndCustomerId(@Param("registrationNo") String registrationNo, 
                                                @Param("customerId") Long customerId);

    /**
     * Check if registration number exists for a customer (excluding specific vehicle)
     */
    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM Vehicle v " +
           "WHERE v.registrationNo = :registrationNo AND v.customer.id = :customerId AND v.vehicleId != :vehicleId")
    boolean existsByRegistrationNoAndCustomerIdAndVehicleIdNot(@Param("registrationNo") String registrationNo, 
                                                                @Param("customerId") Long customerId,
                                                                @Param("vehicleId") Long vehicleId);
}