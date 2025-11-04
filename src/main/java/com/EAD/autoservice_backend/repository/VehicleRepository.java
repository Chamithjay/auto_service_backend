package com.EAD.autoservice_backend.repository;

import com.EAD.autoservice_backend.model.Vehicle;
import com.EAD.autoservice_backend.model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("SELECT v.vehicleType, COUNT(v) FROM Vehicle v GROUP BY v.vehicleType")
    List<Object[]> countByVehicleType();

    List<Vehicle> findAllByOrderByCreatedAtDesc();
}
