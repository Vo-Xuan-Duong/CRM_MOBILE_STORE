package com.example.Backend.dtos.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueReportDto {
    private LocalDate date;
    private BigDecimal totalRevenue;
    private BigDecimal totalCost;
    private BigDecimal profit;
    private BigDecimal profitMargin;
    private Long orderCount;
    private String period; // DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY
}
