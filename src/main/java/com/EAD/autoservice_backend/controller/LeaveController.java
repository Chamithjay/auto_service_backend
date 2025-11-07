package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.EmployeeLeaveRequest;
import com.EAD.autoservice_backend.dto.EmployeeLeaveResponse;
import com.EAD.autoservice_backend.service.LeaveService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for employee leave management.
 * Handles leave request submission.
 */
@RestController
@RequestMapping("/api/v1/leaves")
public class LeaveController {

    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    /**
     * Submits a new leave request from an employee.
     *
     * @param employeeLeaveRequest the leave request details
     * @return ResponseEntity containing the created leave request
     */
    @PostMapping("/request")
    public ResponseEntity<EmployeeLeaveResponse> requestLeave(@Valid @RequestBody EmployeeLeaveRequest employeeLeaveRequest) {
        try {
            EmployeeLeaveResponse employeeLeaveResponse = leaveService.requestLeave(employeeLeaveRequest);
            return new ResponseEntity<>(employeeLeaveResponse, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
