package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.model.*;
import com.EAD.autoservice_backend.repository.AdminRepository;
import com.EAD.autoservice_backend.repository.LeaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class LeaveService {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private AdminRepository adminRepository;

    public java.util.List<Leave> getAllLeaves() {
        return leaveRepository.findAll();
    }

    public Leave updateLeaveStatus(Long leaveId, LeaveStatus status, Long adminId) {
        Optional<Leave> optionalLeave = leaveRepository.findById(leaveId);
        if (optionalLeave.isEmpty()) {
            throw new RuntimeException("Leave not found with ID: " + leaveId);
        }

        Optional<Admin> optionalAdmin = adminRepository.findById(adminId);
        if (optionalAdmin.isEmpty()) {
            throw new RuntimeException("Admin not found with ID: " + adminId);
        }

        Leave leave = optionalLeave.get();
        Admin admin = optionalAdmin.get();

        leave.setLeaveStatus(status);
        leave.setApprovedDate(LocalDate.now());
        leave.setApprovedTime(LocalTime.now());
        leave.setAdmin(admin);

        return leaveRepository.save(leave);
    }
}
