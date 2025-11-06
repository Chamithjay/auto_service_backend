package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.EmployeeLeaveRequest;
import com.EAD.autoservice_backend.dto.EmployeeLeaveResponse;
import com.EAD.autoservice_backend.exception.EmployeeNotFoundException;
import com.EAD.autoservice_backend.model.Employee;
import com.EAD.autoservice_backend.model.Leave;
import com.EAD.autoservice_backend.model.LeaveStatus;
import com.EAD.autoservice_backend.model.LeaveType;
import com.EAD.autoservice_backend.repository.LeaveRepository;
import com.EAD.autoservice_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class LeaveService {

    private final LeaveRepository leaveRepository;
    private final UserRepository userRepository;

    public LeaveService(LeaveRepository leaveRepository, UserRepository userRepository) {
        this.leaveRepository = leaveRepository;
        this.userRepository = userRepository;
    }

    // Save a new leave request from employee.
    public EmployeeLeaveResponse requestLeave(EmployeeLeaveRequest employeeLeaveRequest) {
        try{
            // Fetch the employee.
            Employee employee = (Employee) userRepository.findById(employeeLeaveRequest.getEmployeeId())
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeLeaveRequest.getEmployeeId()));

            Leave leave = new Leave();
            leave.setLeaveDate(employeeLeaveRequest.getLeaveDate());
            leave.setLeaveType(LeaveType.valueOf(employeeLeaveRequest.getLeaveType()));
            leave.setLeaveReason(employeeLeaveRequest.getLeaveReason());
            leave.setEmployee(employee);
            leave.setLeaveStatus(LeaveStatus.NEW);

            Leave savedLeave = leaveRepository.save(leave);

            return new EmployeeLeaveResponse(
                    savedLeave.getLeaveId(),
                    savedLeave.getLeaveType().toString(),
                    savedLeave.getLeaveDate(),
                    savedLeave.getLeaveReason(),
                    savedLeave.getLeaveStatus().toString(),
                    savedLeave.getApprovedTime(),
                    savedLeave.getApprovedDate(),
                    savedLeave.getAdmin() != null ? savedLeave.getAdmin().getUsername() : null
            );
        }catch (Exception e){
            throw new RuntimeException("Failed to request leave: " + e.getMessage());
        }
    }
}
