package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.dtos.salesorder.SalesOrderRequest;
import com.example.Backend.dtos.salesorder.SalesOrderResponse;
import com.example.Backend.models.SalesOrder;
import com.example.Backend.services.SalesOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sales-orders")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Sales Order", description = "Sales Order Management API")
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    @PostMapping
    @PreAuthorize("hasAuthority('SALES_CREATE')")
    @Operation(summary = "Create new sales order")
    public ResponseEntity<ResponseData<SalesOrderResponse>> createOrder(
            @Valid @RequestBody SalesOrderRequest request,
            Authentication authentication) {
        try {
            log.info("Creating new sales order for user: {}", authentication.getName());

            SalesOrderResponse response = salesOrderService.createOrder(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseData.<SalesOrderResponse>builder()
                            .status(HttpStatus.CREATED.value())
                            .message("Sales order created successfully")
                            .data(response)
                            .build());
        } catch (Exception e) {
            log.error("Error creating sales order: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<SalesOrderResponse>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error creating sales order: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SALES_UPDATE')")
    @Operation(summary = "Update sales order")
    public ResponseEntity<ResponseData<SalesOrderResponse>> updateOrder(
            @Parameter(description = "Sales Order ID") @PathVariable Long id,
            @Valid @RequestBody SalesOrderRequest request) {
        try {
            log.info("Updating sales order with ID: {}", id);
            SalesOrderResponse response = salesOrderService.updateOrder(id, request);
            return ResponseEntity.ok(ResponseData.<SalesOrderResponse>builder()
                    .status(HttpStatus.OK.value())
                    .message("Sales order updated successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error updating sales order ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<SalesOrderResponse>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error updating sales order: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SALES_READ')")
    @Operation(summary = "Get sales order by ID")
    public ResponseEntity<ResponseData<SalesOrderResponse>> getOrderById(
            @Parameter(description = "Sales Order ID") @PathVariable Long id) {
        try {
            log.info("Getting sales order with ID: {}", id);
            SalesOrderResponse response = salesOrderService.getOrderById(id);
            return ResponseEntity.ok(ResponseData.<SalesOrderResponse>builder()
                    .status(HttpStatus.OK.value())
                    .message("Sales order retrieved successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error getting sales order ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<SalesOrderResponse>builder()
                            .status(HttpStatus.NOT_FOUND.value())
                            .message("Sales order not found: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SALES_READ')")
    @Operation(summary = "Get all sales orders with pagination")
    public ResponseEntity<ResponseData<Page<SalesOrderResponse>>> getAllOrders(
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("Getting all sales orders with pagination");
            Page<SalesOrderResponse> response = salesOrderService.getAllOrders(pageable);
            return ResponseEntity.ok(ResponseData.<Page<SalesOrderResponse>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Sales orders retrieved successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error getting all sales orders: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Page<SalesOrderResponse>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error getting sales orders: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('SALES_READ')")
    @Operation(summary = "Get orders by status")
    public ResponseEntity<ResponseData<Page<SalesOrderResponse>>> getOrdersByStatus(
            @Parameter(description = "Order Status") @PathVariable String status,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("Getting sales orders with status: {}", status);
            SalesOrder.OrderStatus orderStatus = SalesOrder.OrderStatus.valueOf(status.toUpperCase());
            Page<SalesOrderResponse> response = salesOrderService.getOrdersByStatus(orderStatus, pageable);
            return ResponseEntity.ok(ResponseData.<Page<SalesOrderResponse>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Sales orders by status retrieved successfully")
                    .data(response)
                    .build());
        } catch (IllegalArgumentException e) {
            log.error("Invalid order status: {}", status);
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Page<SalesOrderResponse>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Invalid order status: " + status)
                            .build());
        } catch (Exception e) {
            log.error("Error getting orders by status {}: {}", status, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Page<SalesOrderResponse>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error getting orders by status: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAuthority('SALES_READ')")
    @Operation(summary = "Get orders by customer")
    public ResponseEntity<ResponseData<List<SalesOrderResponse>>> getOrdersByCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long customerId) {
        try {
            log.info("Getting sales orders for customer ID: {}", customerId);
            List<SalesOrderResponse> response = salesOrderService.getOrdersByCustomer(customerId);
            return ResponseEntity.ok(ResponseData.<List<SalesOrderResponse>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Sales orders by customer retrieved successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error getting orders for customer ID {}: {}", customerId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<List<SalesOrderResponse>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error getting orders by customer: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAuthority('SALES_READ')")
    @Operation(summary = "Get orders by date range")
    public ResponseEntity<ResponseData<List<SalesOrderResponse>>> getOrdersByDateRange(
            @Parameter(description = "Start Date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End Date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("Getting sales orders from {} to {}", startDate, endDate);
            if (startDate.isAfter(endDate)) {
                return ResponseEntity.badRequest()
                        .body(ResponseData.<List<SalesOrderResponse>>builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message("Start date cannot be after end date")
                                .build());
            }
            List<SalesOrderResponse> response = salesOrderService.getOrdersByDateRange(startDate, endDate);
            return ResponseEntity.ok(ResponseData.<List<SalesOrderResponse>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Sales orders by date range retrieved successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error getting orders by date range {} to {}: {}", startDate, endDate, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<List<SalesOrderResponse>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error getting orders by date range: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasAuthority('SALES_UPDATE')")
    @Operation(summary = "Confirm sales order")
    public ResponseEntity<ResponseData<SalesOrderResponse>> confirmOrder(
            @Parameter(description = "Sales Order ID") @PathVariable Long id) {
        try {
            log.info("Confirming sales order with ID: {}", id);
            SalesOrderResponse response = salesOrderService.confirmOrder(id);
            return ResponseEntity.ok(ResponseData.<SalesOrderResponse>builder()
                    .status(HttpStatus.OK.value())
                    .message("Sales order confirmed successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error confirming sales order ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<SalesOrderResponse>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error confirming sales order: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/{id}/pay")
    @PreAuthorize("hasAuthority('PAYMENT_CREATE')")
    @Operation(summary = "Mark order as paid")
    public ResponseEntity<ResponseData<SalesOrderResponse>> payOrder(
            @Parameter(description = "Sales Order ID") @PathVariable Long id) {
        try {
            log.info("Marking sales order ID {} as paid", id);
            SalesOrderResponse response = salesOrderService.payOrder(id);
            return ResponseEntity.ok(ResponseData.<SalesOrderResponse>builder()
                    .status(HttpStatus.OK.value())
                    .message("Sales order marked as paid successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error marking sales order ID {} as paid: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<SalesOrderResponse>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error marking order as paid: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('SALES_UPDATE')")
    @Operation(summary = "Cancel sales order")
    public ResponseEntity<ResponseData<String>> cancelOrder(
            @Parameter(description = "Sales Order ID") @PathVariable Long id) {
        try {
            log.info("Cancelling sales order with ID: {}", id);
            salesOrderService.cancelOrder(id);
            return ResponseEntity.ok(ResponseData.<String>builder()
                    .status(HttpStatus.OK.value())
                    .message("Sales order cancelled successfully")
                    .data("Order with ID " + id + " has been cancelled")
                    .build());
        } catch (Exception e) {
            log.error("Error cancelling sales order ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<String>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error cancelling sales order: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/sales-total")
    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    @Operation(summary = "Get total sales")
    public ResponseEntity<ResponseData<BigDecimal>> getTotalSales(
            @Parameter(description = "Start Date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End Date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("Getting total sales from {} to {}", startDate, endDate);
            if (startDate.isAfter(endDate)) {
                return ResponseEntity.badRequest()
                        .body(ResponseData.<BigDecimal>builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message("Start date cannot be after end date")
                                .build());
            }
            BigDecimal total = salesOrderService.getTotalSales(startDate, endDate);
            return ResponseEntity.ok(ResponseData.<BigDecimal>builder()
                    .status(HttpStatus.OK.value())
                    .message("Total sales retrieved successfully")
                    .data(total)
                    .build());
        } catch (Exception e) {
            log.error("Error getting total sales from {} to {}: {}", startDate, endDate, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<BigDecimal>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error getting total sales: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    @Operation(summary = "Get order statistics")
    public ResponseEntity<ResponseData<List<Object[]>>> getOrderStatistics() {
        try {
            log.info("Getting order statistics");
            List<Object[]> stats = salesOrderService.getOrderStatistics();
            return ResponseEntity.ok(ResponseData.<List<Object[]>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Order statistics retrieved successfully")
                    .data(stats)
                    .build());
        } catch (Exception e) {
            log.error("Error getting order statistics: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<List<Object[]>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error getting order statistics: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/reports/daily-sales")
    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    @Operation(summary = "Get daily sales report")
    public ResponseEntity<ResponseData<List<Object[]>>> getDailySalesReport(
            @Parameter(description = "Start Date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End Date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("Getting daily sales report from {} to {}", startDate, endDate);
            if (startDate.isAfter(endDate)) {
                return ResponseEntity.badRequest()
                        .body(ResponseData.<List<Object[]>>builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message("Start date cannot be after end date")
                                .build());
            }
            List<Object[]> report = salesOrderService.getDailySalesReport(startDate, endDate);
            return ResponseEntity.ok(ResponseData.<List<Object[]>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Daily sales report retrieved successfully")
                    .data(report)
                    .build());
        } catch (Exception e) {
            log.error("Error getting daily sales report from {} to {}: {}", startDate, endDate, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<List<Object[]>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error getting daily sales report: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/reports/sales-performance")
    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    @Operation(summary = "Get sales performance by user")
    public ResponseEntity<ResponseData<List<Object[]>>> getSalesPerformanceByUser(
            @Parameter(description = "Start Date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End Date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("Getting sales performance report from {} to {}", startDate, endDate);
            if (startDate.isAfter(endDate)) {
                return ResponseEntity.badRequest()
                        .body(ResponseData.<List<Object[]>>builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message("Start date cannot be after end date")
                                .build());
            }
            List<Object[]> performance = salesOrderService.getSalesPerformanceByUser(startDate, endDate);
            return ResponseEntity.ok(ResponseData.<List<Object[]>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Sales performance report retrieved successfully")
                    .data(performance)
                    .build());
        } catch (Exception e) {
            log.error("Error getting sales performance report from {} to {}: {}", startDate, endDate, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<List<Object[]>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error getting sales performance report: " + e.getMessage())
                            .build());
        }
    }

}
