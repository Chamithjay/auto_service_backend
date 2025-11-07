package com.EAD.autoservice_backend.dto;

import com.EAD.autoservice_backend.model.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveReport {
    private LeaveType leaveType;
    private Long count;
}
