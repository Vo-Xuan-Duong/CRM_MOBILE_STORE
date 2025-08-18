package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.dtos.installment.InstallmentPlanRequest;
import com.example.Backend.dtos.installment.InstallmentPlanResponse;
import com.example.Backend.services.InstallmentPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/installment-plans")
@RequiredArgsConstructor
@Tag(name = "Installment Plan", description = "Installment Plan Management API")
public class InstallmentPlanController {

    private final InstallmentPlanService installmentPlanService;

    @PostMapping
    @PreAuthorize("hasAuthority('PAYMENT_CREATE')")
    @Operation(summary = "Create installment plan")
    public ResponseEntity<ResponseData<InstallmentPlanResponse>> createInstallmentPlan(
            @Valid @RequestBody InstallmentPlanRequest request) {
        InstallmentPlanResponse response = installmentPlanService.createInstallmentPlan(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<InstallmentPlanResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Installment plan created successfully")
                        .data(response)
                        .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PAYMENT_READ')")
    @Operation(summary = "Get installment plan by ID")
    public ResponseEntity<ResponseData<InstallmentPlanResponse>> getInstallmentPlanById(@PathVariable Long id) {
        InstallmentPlanResponse response = installmentPlanService.getInstallmentPlanById(id);
        return ResponseEntity.ok(ResponseData.<InstallmentPlanResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Installment plan retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAuthority('PAYMENT_READ')")
    @Operation(summary = "Get installment plan by order ID")
    public ResponseEntity<ResponseData<InstallmentPlanResponse>> getInstallmentPlanByOrderId(@PathVariable Long orderId) {
        InstallmentPlanResponse response = installmentPlanService.getInstallmentPlanByOrderId(orderId);
        return ResponseEntity.ok(ResponseData.<InstallmentPlanResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Installment plan by order retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PAYMENT_READ')")
    @Operation(summary = "Get all installment plans")
    public ResponseEntity<ResponseData<Page<InstallmentPlanResponse>>> getAllInstallmentPlans(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<InstallmentPlanResponse> response = installmentPlanService.getAllInstallmentPlans(pageable);
        return ResponseEntity.ok(ResponseData.<Page<InstallmentPlanResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Installment plans retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('PAYMENT_READ')")
    @Operation(summary = "Get installment plans by status")
    public ResponseEntity<ResponseData<List<InstallmentPlanResponse>>> getInstallmentPlansByStatus(
            @PathVariable String status) {
        List<InstallmentPlanResponse> response = installmentPlanService.getInstallmentPlansByStatus(status);
        return ResponseEntity.ok(ResponseData.<List<InstallmentPlanResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Installment plans by status retrieved successfully")
                .data(response)
                .build());
    }

    @PutMapping("/{id}/complete")
    @PreAuthorize("hasAuthority('PAYMENT_UPDATE')")
    @Operation(summary = "Complete installment plan")
    public ResponseEntity<ResponseData<Void>> completeInstallmentPlan(@PathVariable Long id) {
        installmentPlanService.completeInstallmentPlan(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Installment plan completed successfully")
                .build());
    }

    @PutMapping("/{id}/default")
    @PreAuthorize("hasAuthority('PAYMENT_UPDATE')")
    @Operation(summary = "Mark installment plan as defaulted")
    public ResponseEntity<ResponseData<Void>> markAsDefaulted(@PathVariable Long id) {
        installmentPlanService.markAsDefaulted(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Installment plan marked as defaulted")
                .build());
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    @Operation(summary = "Get installment statistics")
    public ResponseEntity<ResponseData<InstallmentStatistics>> getInstallmentStatistics() {
        InstallmentStatistics stats = installmentPlanService.getInstallmentStatistics();
        return ResponseEntity.ok(ResponseData.<InstallmentStatistics>builder()
                .status(HttpStatus.OK.value())
                .message("Installment statistics retrieved successfully")
                .data(stats)
                .build());
    }

    @lombok.Data
    @lombok.Builder
    public static class InstallmentStatistics {
        private long totalPlans;
        private long activePlans;
        private long completedPlans;
        private long defaultedPlans;
        private BigDecimal totalPrincipal;
        private BigDecimal totalOutstanding;
    }
}
