package com.EAD.autoservice_backend.dto;

import java.math.BigDecimal;

public class MonthlyRevenueResponse {
    private String month; // "Jan", "Feb", etc.
    private BigDecimal revenue;

    public MonthlyRevenueResponse() {}

    public MonthlyRevenueResponse(String month, BigDecimal revenue) {
        this.month = month;
        this.revenue = revenue;
    }

    // Getters and Setters
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
}