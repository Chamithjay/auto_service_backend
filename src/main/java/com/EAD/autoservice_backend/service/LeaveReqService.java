package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.LeaveReqDTO;
import com.EAD.autoservice_backend.model.*;
import com.EAD.autoservice_backend.repository.AdminRepository;
import com.EAD.autoservice_backend.repository.LeaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LeaveReqService {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private AdminRepository adminRepository;

public List<LeaveReqDTO> getAllLeaves() {
    List<Leave> leaves = leaveRepository.findAll();
    List<LeaveReqDTO> dtos = new ArrayList<>();

    for (Leave leave : leaves) {
        LeaveReqDTO dto = new LeaveReqDTO();
        dto.setLeaveId(leave.getLeaveId());
        dto.setLeaveType(leave.getLeaveType());
        dto.setLeaveDate(leave.getLeaveDate());
        dto.setLeaveReason(leave.getLeaveReason());
        dto.setApprovedDate(leave.getApprovedDate());
        dto.setApprovedTime(leave.getApprovedTime());
        dto.setLeaveStatus(leave.getLeaveStatus());

        // ✅ Include employee ID and username
        if (leave.getEmployee() != null) {
            dto.setEmployeeId(leave.getEmployee().getId().toString());
            dto.setUsername(leave.getEmployee().getUsername());
        }

        dtos.add(dto);
    }

    return dtos;
}

@Autowired
private EmailService emailService;

    public Leave updateLeaveStatus(Long leaveId, LeaveStatus status) {
        Optional<Leave> optionalLeave = leaveRepository.findById(leaveId);
        if (optionalLeave.isEmpty()) {
            throw new RuntimeException("Leave not found with ID: " + leaveId);
        }

        Leave leave = optionalLeave.get();

        leave.setLeaveStatus(status);
        leave.setApprovedDate(LocalDate.now());
        leave.setApprovedTime(LocalTime.now());

        // Save the updated leave
        Leave updatedLeave = leaveRepository.save(leave);

        // ✅ Send email notification
        if (leave.getEmployee() != null && leave.getEmployee().getEmail() != null) {
            String employeeEmail = leave.getEmployee().getEmail();
            String subject;
            String messageBody;

            if (status == LeaveStatus.APPROVED) {
                subject = "Your Leave Request has been Approved ";
                messageBody = String.format(
                        "Dear %s,\n\nYour leave request on %s has been approved.\n\nBest regards,\nHR Department",
                        leave.getEmployee().getUsername(), leave.getLeaveDate()
                );
            } else if (status == LeaveStatus.REJECTED) {
                subject = "Your Leave Request has been Rejected ";
                messageBody = String.format(
                        "Dear %s,\n\nWe regret to inform you that your leave request on %s has been rejected.\n\nBest regards,\nHR Department",
                        leave.getEmployee().getUsername(), leave.getLeaveDate()
                );
            } else {
                subject = "Leave Request Status Updated";
                messageBody = String.format(
                        "Dear %s,\n\nYour leave request on %s is now marked as %s.\n\nBest regards,\nHR Department",
                        leave.getEmployee().getUsername(), leave.getLeaveDate(), status
                );
            }

            emailService.sendEmail(employeeEmail, subject, messageBody);
        }

        return updatedLeave;
    }
}
