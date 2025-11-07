package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.dto.DashboardStatsResponse;
import com.EAD.autoservice_backend.dto.MonthlyRevenueResponse;
import com.EAD.autoservice_backend.dto.RecentActivityResponse;
import com.EAD.autoservice_backend.dto.VehicleTypeDistributionResponse;
import com.EAD.autoservice_backend.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for Dashboard data
 */
@RestController
@RequestMapping("/api/v1/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Get main dashboard statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        DashboardStatsResponse stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Get recent activities
     */
    @GetMapping("/recent-activities")
    public ResponseEntity<List<RecentActivityResponse>> getRecentActivities() {
        List<RecentActivityResponse> activities = dashboardService.getRecentActivities();
        return ResponseEntity.ok(activities);
    }

    /**
     * Get monthly revenue chart data
     */
    @GetMapping("/monthly-revenue")
    public ResponseEntity<List<MonthlyRevenueResponse>> getMonthlyRevenue() {
        List<MonthlyRevenueResponse> revenue = dashboardService.getMonthlyRevenue();
        return ResponseEntity.ok(revenue);
    }

    /**
     * Get vehicle type distribution
     */
    @GetMapping("/vehicle-distribution")
    public ResponseEntity<List<VehicleTypeDistributionResponse>> getVehicleDistribution() {
        List<VehicleTypeDistributionResponse> distribution = dashboardService.getVehicleTypeDistribution();
        return ResponseEntity.ok(distribution);
    }
}