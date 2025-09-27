package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.ReportDTO;
import com.EAD.autoservice_backend.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasRole('ADMIN')") // Secures all report endpoints
public class ReportController {

    @Autowired
    private ReportService reportService;

    // This creates the endpoint: GET /api/reports/service-popularity
    @GetMapping("/service-popularity")
    public ResponseEntity<List<ReportDTO>> getServicePopularityReport() {
        List<ReportDTO> reportData = reportService.getServicePopularity();
        return ResponseEntity.ok(reportData);
    }
}