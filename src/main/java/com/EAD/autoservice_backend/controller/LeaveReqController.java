package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.LeaveReqDTO;
import com.EAD.autoservice_backend.model.Leave;
import com.EAD.autoservice_backend.model.LeaveStatus;
import com.EAD.autoservice_backend.service.LeaveReqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for leave request management.
 * Handles leave retrieval, approval, and rejection operations.
 */
@RestController
@RequestMapping("/api/v1/leaves")
@CrossOrigin(origins = "*")
public class LeaveReqController {

    @Autowired
    private LeaveReqService leaveService;

    /**
     * Retrieves all leave requests.
     *
     * @return list of all leave requests
     */
    @GetMapping
    public List<LeaveReqDTO> getAllLeaves() {
        return leaveService.getAllLeaves();
    }

    /**
     * Approves a leave request.
     *
     * @param leaveId the ID of the leave request to approve
     * @return the approved leave request
     */
    @PutMapping("/{leaveId}/approve")
    public Leave approveLeave(@PathVariable Long leaveId) {
        return leaveService.updateLeaveStatus(leaveId, LeaveStatus.APPROVED);
    }

    /**
     * Rejects a leave request.
     *
     * @param leaveId the ID of the leave request to reject
     * @return the rejected leave request
     */
    @PutMapping("/{leaveId}/reject")
    public Leave rejectLeave(@PathVariable Long leaveId) {
        return leaveService.updateLeaveStatus(leaveId, LeaveStatus.REJECTED);
    }

}