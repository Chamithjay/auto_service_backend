package com.EAD.autoservice_backend.repository;

import com.EAD.autoservice_backend.model.ServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceItemRepository extends JpaRepository<ServiceItem,Long> {

}
