package com.EAD.autoservice_backend.repository;

import com.EAD.autoservice_backend.model.ServiceItem;
import com.EAD.autoservice_backend.model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long> {
    List<ServiceItem> findByVehicleType(VehicleType vehicleType);

}



