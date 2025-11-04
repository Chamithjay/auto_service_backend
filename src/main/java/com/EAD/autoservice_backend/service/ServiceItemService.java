package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.EmployeeServiceItemResponse;
import com.EAD.autoservice_backend.exception.ServiceItemNotFoundException;
import com.EAD.autoservice_backend.model.ServiceItem;
import com.EAD.autoservice_backend.repository.ServiceItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceItemService {

    private final ServiceItemRepository serviceItemRepository;

    public ServiceItemService(ServiceItemRepository serviceItemRepository) {
        this.serviceItemRepository = serviceItemRepository;
    }

    // Get service item deetails using the servie item ID.
    public EmployeeServiceItemResponse getServiceItemById(Long serviceItemId) {
        ServiceItem serviceItem = serviceItemRepository.findById(serviceItemId)
                .orElseThrow(() -> new ServiceItemNotFoundException("Service Item  not found with ID: " + serviceItemId));

        return new EmployeeServiceItemResponse(
                serviceItem.getServiceItemId(),
                serviceItem.getServiceItemName(),
                serviceItem.getEstimatedDuration()
        );
    }
}
