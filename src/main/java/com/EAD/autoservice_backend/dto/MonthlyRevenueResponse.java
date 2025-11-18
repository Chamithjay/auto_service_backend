package com.EAD.autoservice_backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class MonthlyRevenueResponse {
    // Getters and Setters
    private String month; // "Jan", "Feb", etc.
    private BigDecimal revenue;

    public MonthlyRevenueResponse() {}

    public MonthlyRevenueResponse(String month, BigDecimal revenue) {
        this.month = month;
        this.revenue = revenue;
    }

}