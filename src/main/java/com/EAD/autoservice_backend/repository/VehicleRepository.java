package com.EAD.autoservice_backend.repository;

import com.EAD.autoservice_backend.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByCustomerId(Long customerId);
    Vehicle findByVehicleId(Long vehicleId);
}


