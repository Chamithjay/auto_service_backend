package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.dto.EmployeePerformance;
import com.EAD.autoservice_backend.dto.LeaveReport;
import com.EAD.autoservice_backend.dto.RevenueOverTime;
import com.EAD.autoservice_backend.dto.ServicePopularity;
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
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = LocalDate.now().minusDays(range);
        return appointmentJobRepository.getServicePopularity(startDate, endDate);
    }

    public List<EmployeePerformance> getEmployeePerformance(Integer range) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = LocalDate.now().minusDays(range);
        return jobAssignmentRepository.getEmployeePerformance(startDate, endDate);
    }

    public List<RevenueOverTime> getRevenueOverTime(Integer range) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = LocalDate.now().minusDays(range);
        return appointmentRepository.getRevenueOverTime(startDate, endDate);
    }

    public List<LeaveReport> getLeaveReport(Integer range) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = LocalDate.now().minusDays(range);
        return leaveRepository.getLeaveReport(startDate, endDate);
    }
}