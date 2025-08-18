package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.dtos.report.*;
import com.example.Backend.services.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "APIs for statistical reports and analytics")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard statistics",
               description = "Retrieve overall statistics for the dashboard including customers, orders, revenue, etc.")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ResponseData<DashboardStatsDto>> getDashboardStats() {
        try {
            DashboardStatsDto stats = reportService.getDashboardStats();
            return ResponseEntity.ok(ResponseData.<DashboardStatsDto>builder()
                    .status(200)
                    .message("Dashboard statistics retrieved successfully")
                    .data(stats)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ResponseData.<DashboardStatsDto>builder()
                            .status(500)
                            .message("Error retrieving dashboard statistics: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/sales")
    @Operation(summary = "Get sales report",
               description = "Retrieve sales report by date range and period (DAILY, WEEKLY, MONTHLY, YEARLY)")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES')")
    public ResponseEntity<ResponseData<List<SalesReportDto>>> getSalesReport(
            @Parameter(description = "Start date for the report", example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "End date for the report", example = "2024-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @Parameter(description = "Period type", example = "DAILY")
            @RequestParam(defaultValue = "DAILY") String period) {
        try {
            List<SalesReportDto> report = reportService.getSalesReport(startDate, endDate, period);
            return ResponseEntity.ok(ResponseData.<List<SalesReportDto>>builder()
                    .status(200)
                    .message("Sales report retrieved successfully")
                    .data(report)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ResponseData.<List<SalesReportDto>>builder()
                            .status(500)
                            .message("Error retrieving sales report: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/customers")
    @Operation(summary = "Get customer report",
               description = "Retrieve customer analysis report with spending patterns and segments")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES')")
    public ResponseEntity<ResponseData<List<CustomerReportDto>>> getCustomerReport(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "Sort field", example = "totalSpent")
            @RequestParam(defaultValue = "totalSpent") String sortBy,

            @Parameter(description = "Sort direction", example = "DESC")
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        try {
            List<CustomerReportDto> report = reportService.getCustomerReport(page, size, sortBy, sortDirection);
            return ResponseEntity.ok(ResponseData.<List<CustomerReportDto>>builder()
                    .status(200)
                    .message("Customer report retrieved successfully")
                    .data(report)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ResponseData.<List<CustomerReportDto>>builder()
                            .status(500)
                            .message("Error retrieving customer report: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/products")
    @Operation(summary = "Get product report",
               description = "Retrieve product performance report including sales, stock status, and profitability")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'INVENTORY')")
    public ResponseEntity<ResponseData<List<ProductReportDto>>> getProductReport(
            @Parameter(description = "Product category filter", example = "SMARTPHONE")
            @RequestParam(required = false) String category,

            @Parameter(description = "Sort field", example = "revenue")
            @RequestParam(defaultValue = "revenue") String sortBy,

            @Parameter(description = "Sort direction", example = "DESC")
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        try {
            List<ProductReportDto> report = reportService.getProductReport(category, sortBy, sortDirection);
            return ResponseEntity.ok(ResponseData.<List<ProductReportDto>>builder()
                    .status(200)
                    .message("Product report retrieved successfully")
                    .data(report)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ResponseData.<List<ProductReportDto>>builder()
                            .status(500)
                            .message("Error retrieving product report: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/revenue")
    @Operation(summary = "Get revenue report",
               description = "Retrieve revenue and profit analysis by date range and period")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ResponseData<List<RevenueReportDto>>> getRevenueReport(
            @Parameter(description = "Start date for the report", example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "End date for the report", example = "2024-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @Parameter(description = "Period type", example = "MONTHLY")
            @RequestParam(defaultValue = "MONTHLY") String period) {
        try {
            List<RevenueReportDto> report = reportService.getRevenueReport(startDate, endDate, period);
            return ResponseEntity.ok(ResponseData.<List<RevenueReportDto>>builder()
                    .status(200)
                    .message("Revenue report retrieved successfully")
                    .data(report)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ResponseData.<List<RevenueReportDto>>builder()
                            .status(500)
                            .message("Error retrieving revenue report: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/top-products")
    @Operation(summary = "Get top selling products",
               description = "Retrieve list of best performing products by revenue")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES')")
    public ResponseEntity<ResponseData<List<TopProductDto>>> getTopProducts(
            @Parameter(description = "Number of top products to retrieve", example = "10")
            @RequestParam(defaultValue = "10") int limit,

            @Parameter(description = "Start date for analysis", example = "2024-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "End date for analysis", example = "2024-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<TopProductDto> report = reportService.getTopProducts(limit, startDate, endDate);
            return ResponseEntity.ok(ResponseData.<List<TopProductDto>>builder()
                    .status(200)
                    .message("Top products retrieved successfully")
                    .data(report)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ResponseData.<List<TopProductDto>>builder()
                            .status(500)
                            .message("Error retrieving top products: " + e.getMessage())
                            .build());
        }
    }
}
