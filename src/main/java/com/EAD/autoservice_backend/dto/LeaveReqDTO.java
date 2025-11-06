package com.EAD.autoservice_backend.dto;

import com.EAD.autoservice_backend.model.LeaveStatus;
import com.EAD.autoservice_backend.model.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveReqDTO {
    private Long leaveId;
    private LeaveType leaveType;
    private LocalDate leaveDate;
    private String leaveReason;
    private LocalTime approvedTime;
    private LocalDate approvedDate;
    private LeaveStatus leaveStatus;
    private String employeeName;
    private String adminName;
}
