package com.EAD.autoservice_backend.dto;

import com.EAD.autoservice_backend.model.Admin;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalTime;

@Value
public class EmployeeLeaveResponse {

    @NotNull
    private final Long leaveId;
    @NotNull
    private final String leaveType;
    @NotNull
    private final LocalDate leaveDate;
    @NotNull
    private final String leaveReason;
    @NotNull
    private final String leaveStatus;

}
