package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.dtos.order.SalesOrderRequest;
import com.example.Backend.dtos.order.SalesOrderResponse;
import com.example.Backend.services.SalesOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sales-orders")
@RequiredArgsConstructor
@Tag(name = "Sales Order", description = "Sales Order Management API")
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    @PostMapping
    @PreAuthorize("hasAuthority('SALES_CREATE')")
    @Operation(summary = "Create new sales order")
    public ResponseEntity<ResponseData<SalesOrderResponse>> createOrder(
            @Valid @RequestBody SalesOrderRequest request,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        SalesOrderResponse response = salesOrderService.createOrder(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<SalesOrderResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Sales order created successfully")
                        .data(response)
                        .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SALES_UPDATE')")
    @Operation(summary = "Update sales order")
    public ResponseEntity<ResponseData<SalesOrderResponse>> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody SalesOrderRequest request) {
        SalesOrderResponse response = salesOrderService.updateOrder(id, request);
        return ResponseEntity.ok(ResponseData.<SalesOrderResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Sales order updated successfully")
                .data(response)
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SALES_READ')")
    @Operation(summary = "Get sales order by ID")
    public ResponseEntity<ResponseData<SalesOrderResponse>> getOrderById(@PathVariable Long id) {
        SalesOrderResponse response = salesOrderService.getOrderById(id);
        return ResponseEntity.ok(ResponseData.<SalesOrderResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Sales order retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/number/{orderNumber}")
    @PreAuthorize("hasAuthority('SALES_READ')")
    @Operation(summary = "Get sales order by order number")
    public ResponseEntity<ResponseData<SalesOrderResponse>> getOrderByNumber(@PathVariable String orderNumber) {
        SalesOrderResponse response = salesOrderService.getOrderByNumber(orderNumber);
        return ResponseEntity.ok(ResponseData.<SalesOrderResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Sales order retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SALES_READ')")
    @Operation(summary = "Get all sales orders with pagination")
    public ResponseEntity<ResponseData<Page<SalesOrderResponse>>> getAllOrders(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<SalesOrderResponse> response = salesOrderService.getAllOrders(pageable);
        return ResponseEntity.ok(ResponseData.<Page<SalesOrderResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Sales orders retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('SALES_READ')")
    @Operation(summary = "Get orders by status")
    public ResponseEntity<ResponseData<Page<SalesOrderResponse>>> getOrdersByStatus(
            @PathVariable String status,
            @PageableDefault(size = 20) Pageable pageable) {
        SalesOrderResponse.OrderStatus orderStatus = SalesOrderResponse.OrderStatus.valueOf(status.toUpperCase());
        Page<SalesOrderResponse> response = salesOrderService.getOrdersByStatus(orderStatus, pageable);
        return ResponseEntity.ok(ResponseData.<Page<SalesOrderResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Sales orders by status retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAuthority('SALES_READ')")
    @Operation(summary = "Get orders by customer")
    public ResponseEntity<ResponseData<List<SalesOrderResponse>>> getOrdersByCustomer(
            @PathVariable Long customerId) {
        List<SalesOrderResponse> response = salesOrderService.getOrdersByCustomer(customerId);
        return ResponseEntity.ok(ResponseData.<List<SalesOrderResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Sales orders by customer retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAuthority('SALES_READ')")
    @Operation(summary = "Get orders by date range")
    public ResponseEntity<ResponseData<List<SalesOrderResponse>>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<SalesOrderResponse> response = salesOrderService.getOrdersByDateRange(startDate, endDate);
        return ResponseEntity.ok(ResponseData.<List<SalesOrderResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Sales orders by date range retrieved successfully")
                .data(response)
                .build());
    }

    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasAuthority('SALES_UPDATE')")
    @Operation(summary = "Confirm sales order")
    public ResponseEntity<ResponseData<SalesOrderResponse>> confirmOrder(@PathVariable Long id) {
        SalesOrderResponse response = salesOrderService.confirmOrder(id);
        return ResponseEntity.ok(ResponseData.<SalesOrderResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Sales order confirmed successfully")
                .data(response)
                .build());
    }

    @PutMapping("/{id}/pay")
    @PreAuthorize("hasAuthority('PAYMENT_CREATE')")
    @Operation(summary = "Mark order as paid")
    public ResponseEntity<ResponseData<SalesOrderResponse>> payOrder(@PathVariable Long id) {
        SalesOrderResponse response = salesOrderService.payOrder(id);
        return ResponseEntity.ok(ResponseData.<SalesOrderResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Sales order marked as paid successfully")
                .data(response)
                .build());
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('SALES_UPDATE')")
    @Operation(summary = "Cancel sales order")
    public ResponseEntity<ResponseData<Void>> cancelOrder(@PathVariable Long id) {
        salesOrderService.cancelOrder(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Sales order cancelled successfully")
                .build());
    }

    @GetMapping("/sales-total")
    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    @Operation(summary = "Get total sales")
    public ResponseEntity<ResponseData<BigDecimal>> getTotalSales(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        BigDecimal total = salesOrderService.getTotalSales(startDate, endDate);
        return ResponseEntity.ok(ResponseData.<BigDecimal>builder()
                .status(HttpStatus.OK.value())
                .message("Total sales retrieved successfully")
                .data(total)
                .build());
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    @Operation(summary = "Get order statistics")
    public ResponseEntity<ResponseData<List<Object[]>>> getOrderStatistics() {
        List<Object[]> stats = salesOrderService.getOrderStatistics();
        return ResponseEntity.ok(ResponseData.<List<Object[]>>builder()
                .status(HttpStatus.OK.value())
                .message("Order statistics retrieved successfully")
                .data(stats)
                .build());
    }

    @GetMapping("/reports/daily-sales")
    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    @Operation(summary = "Get daily sales report")
    public ResponseEntity<ResponseData<List<Object[]>>> getDailySalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> report = salesOrderService.getDailySalesReport(startDate, endDate);
        return ResponseEntity.ok(ResponseData.<List<Object[]>>builder()
                .status(HttpStatus.OK.value())
                .message("Daily sales report retrieved successfully")
                .data(report)
                .build());
    }

    @GetMapping("/reports/sales-performance")
    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    @Operation(summary = "Get sales performance by user")
    public ResponseEntity<ResponseData<List<Object[]>>> getSalesPerformanceByUser(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> performance = salesOrderService.getSalesPerformanceByUser(startDate, endDate);
        return ResponseEntity.ok(ResponseData.<List<Object[]>>builder()
                .status(HttpStatus.OK.value())
                .message("Sales performance report retrieved successfully")
                .data(performance)
                .build());
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        // Implementation to extract user ID from authentication
        return 1L; // Placeholder
    }
}
