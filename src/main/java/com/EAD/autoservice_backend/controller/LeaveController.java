package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.model.Admin;
import com.EAD.autoservice_backend.model.Leave;
import com.EAD.autoservice_backend.model.LeaveStatus;
import com.EAD.autoservice_backend.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leaves")
@CrossOrigin(origins = "*")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    // Fetch all leaves
    @GetMapping
    public java.util.List<Leave> getAllLeaves() {
        return leaveService.getAllLeaves();
    }

    // ✅ Approve a leave
    @PutMapping("/{leaveId}/approve")
    public Leave approveLeave(@PathVariable Long leaveId, @RequestParam Long adminId) {
        return leaveService.updateLeaveStatus(leaveId, LeaveStatus.APPROVED, adminId);
    }


    // ✅ Reject a leave (fixed)
    @PutMapping("/{leaveId}/reject")
    public Leave rejectLeave(@PathVariable Long leaveId, @RequestParam Long adminId) {
        return leaveService.updateLeaveStatus(leaveId, LeaveStatus.REJECTED, adminId);
    }
}

