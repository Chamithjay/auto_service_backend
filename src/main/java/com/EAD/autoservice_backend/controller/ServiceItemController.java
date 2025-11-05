package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.EmployeeServiceItemResponse;
import com.EAD.autoservice_backend.service.ServiceItemService;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/service-items")
public class ServiceItemController {

    private final ServiceItemService serviceItemService;

    public ServiceItemController(ServiceItemService serviceItemService) {
        this.serviceItemService = serviceItemService;
    }

    // Get service item information by id.
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeServiceItemResponse> getServiceItemById(@PathVariable("id") Long serviceItemId) {
        try {
            EmployeeServiceItemResponse employeeServiceItemResponse = serviceItemService.getServiceItemById(serviceItemId);
            return new ResponseEntity<>(employeeServiceItemResponse, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
