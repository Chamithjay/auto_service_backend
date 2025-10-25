package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.ServiceItemResponse;
import com.EAD.autoservice_backend.service.ServiceItemService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/service-items")
public class ServiceItemController {

    private final ServiceItemService serviceItemService;

    @Autowired
    public ServiceItemController(ServiceItemService serviceItemService) {
        this.serviceItemService = serviceItemService;
    }

    // Get service item information by id.
    @GetMapping("/{id}")
    public ResponseEntity<ServiceItemResponse> getServiceItemById(@PathVariable("id") Long serviceItemId) {
        try{
            System.out.println("hello" + serviceItemId);
            ServiceItemResponse serviceItemResponse = serviceItemService.getServiceItemById(serviceItemId);
            return ResponseEntity.ok(serviceItemResponse);

        }catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }
}
