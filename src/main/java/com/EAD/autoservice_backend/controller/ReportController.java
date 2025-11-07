package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.EmployeePerformance;
import com.EAD.autoservice_backend.dto.LeaveReport;
import com.EAD.autoservice_backend.dto.RevenueOverTime;
import com.EAD.autoservice_backend.dto.ServicePopularity;
import com.EAD.autoservice_backend.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/service-popularity")
    public ResponseEntity<List<ServicePopularity>> getServicePopularity(
            @RequestParam(defaultValue = "30") Integer range) {
        log.info("GET /api/reports/service-popularity for last {} days", range);
        return ResponseEntity.ok(reportService.getServicePopularity(range));
    }

    @GetMapping("/employee-performance")
    public ResponseEntity<List<EmployeePerformance>> getEmployeePerformance(
            @RequestParam(defaultValue = "30") Integer range) {
        log.info("GET /api/reports/employee-performance for last {} days", range);
        return ResponseEntity.ok(reportService.getEmployeePerformance(range));
    }

    @GetMapping("/revenue-over-time")
    public ResponseEntity<List<RevenueOverTime>> getRevenueOverTime(
            @RequestParam(defaultValue = "30") Integer range) {
        log.info("GET /api/reports/revenue-over-time for last {} days", range);
        return ResponseEntity.ok(reportService.getRevenueOverTime(range));
    }

    @GetMapping("/leave-report")
    public ResponseEntity<List<LeaveReport>> getLeaveReport(
            @RequestParam(defaultValue = "30") Integer range) {
        log.info("GET /api/reports/leave-report for last {} days", range);
        return ResponseEntity.ok(reportService.getLeaveReport(range));
    }
}