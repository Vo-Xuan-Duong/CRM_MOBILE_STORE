package com.example.Backend.dtos.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {
    private Long totalCustomers;
    private Long totalOrders;
    private Long totalProducts;
    private BigDecimal totalRevenue;
    private BigDecimal monthlyRevenue;
    private Long pendingOrders;
    private Long completedOrders;
    private Long cancelledOrders;
    private Long activeCustomers;
    private Long lowStockProducts;
    private Double averageOrderValue;
    private Integer customerGrowthRate; // Percentage
    private Integer revenueGrowthRate; // Percentage
}
