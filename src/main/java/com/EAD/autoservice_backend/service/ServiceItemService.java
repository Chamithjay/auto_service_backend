package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.ServiceItemResponse;
import com.EAD.autoservice_backend.model.ServiceItem;
import com.EAD.autoservice_backend.repository.ServiceItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceItemService {

    private final ServiceItemRepository serviceItemRepository;

    @Autowired
    public ServiceItemService(ServiceItemRepository serviceItemRepository) {
        this.serviceItemRepository = serviceItemRepository;
    }

    public ServiceItemResponse getServiceItemById(Long serviceItemId) {
        ServiceItem serviceItem = serviceItemRepository.findById(serviceItemId)
                .orElseThrow(() -> new EntityNotFoundException("Service Item  not found with ID: " + serviceItemId));

        return new ServiceItemResponse(
                serviceItem.getServiceItemId(),
                serviceItem.getServiceItemName()
        );
    }
}
