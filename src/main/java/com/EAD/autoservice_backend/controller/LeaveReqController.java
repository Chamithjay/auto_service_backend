package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.LeaveReqDTO;
import com.EAD.autoservice_backend.model.Leave;
import com.EAD.autoservice_backend.model.LeaveStatus;
import com.EAD.autoservice_backend.service.LeaveReqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leaves")
@CrossOrigin(origins = "*")
public class LeaveReqController {

    @Autowired
    private LeaveReqService leaveService;

    //get all leaves
    @GetMapping
    public List<LeaveReqDTO> getAllLeaves() {
        return leaveService.getAllLeaves();
    }

    //approve a leave
    @PutMapping("/{leaveId}/approve")
    public Leave approveLeave(@PathVariable Long leaveId) {
        return leaveService.updateLeaveStatus(leaveId, LeaveStatus.APPROVED);
    }

    //reject a leave
    @PutMapping("/{leaveId}/reject")
    public Leave rejectLeave(@PathVariable Long leaveId) {
        return leaveService.updateLeaveStatus(leaveId, LeaveStatus.REJECTED);
    }

}