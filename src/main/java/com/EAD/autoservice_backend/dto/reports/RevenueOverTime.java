// In RevenueOverTime.java
package com.EAD.autoservice_backend.dto.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal; // <-- Import this
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevenueOverTime {
    private LocalDate date;
    private BigDecimal totalRevenue; // <-- Change this to BigDecimal
}