package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.reports.EmployeePerformance;
import com.EAD.autoservice_backend.dto.reports.LeaveReport;
import com.EAD.autoservice_backend.dto.reports.RevenueOverTime;
import com.EAD.autoservice_backend.dto.reports.ServicePopularity;
import com.EAD.autoservice_backend.exception.BadRequestException;
import com.EAD.autoservice_backend.repository.AppointmentJobRepository;
import com.EAD.autoservice_backend.repository.AppointmentRepository;
import com.EAD.autoservice_backend.repository.JobAssignmentRepository;
import com.EAD.autoservice_backend.repository.LeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final AppointmentJobRepository appointmentJobRepository;
    private final JobAssignmentRepository jobAssignmentRepository;
    private final AppointmentRepository appointmentRepository;
    private final LeaveRepository leaveRepository;

    public List<ServicePopularity> getServicePopularity(Integer range) {
        DateRange dr = validateAndComputeRange(range);
        return appointmentJobRepository.getServicePopularity(dr.start(), dr.end());
    }

    public List<EmployeePerformance> getEmployeePerformance(Integer range) {
        DateRange dr = validateAndComputeRange(range);
        return jobAssignmentRepository.getEmployeePerformance(dr.start(), dr.end());
    }

    public List<RevenueOverTime> getRevenueOverTime(Integer range) {
        DateRange dr = validateAndComputeRange(range);
        return appointmentRepository.getRevenueOverTime(dr.start(), dr.end());
    }

    public List<LeaveReport> getLeaveReport(Integer range) {
        DateRange dr = validateAndComputeRange(range);
        return leaveRepository.getLeaveReport(dr.start(), dr.end());
    }

    private DateRange validateAndComputeRange(Integer range) {
        if (range == null) {
            throw new BadRequestException("Range cannot be null");
        }
        if (range < 0) {
            throw new BadRequestException("Range must be non-negative");
        }
        if (range > 365) { // simple guardrail
            throw new BadRequestException("Range exceeds maximum allowed (365 days)");
        }
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(range);
        return new DateRange(start, end);
    }

    private record DateRange(LocalDate start, LocalDate end) {}
}