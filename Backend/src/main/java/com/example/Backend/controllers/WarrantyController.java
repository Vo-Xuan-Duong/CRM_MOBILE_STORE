package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.dtos.warranty.WarrantyRequest;
import com.example.Backend.dtos.warranty.WarrantyResponse;
import com.example.Backend.services.WarrantyService;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/warranties")
@RequiredArgsConstructor
@Tag(name = "Warranty", description = "Warranty Management API")
public class WarrantyController {

    private final WarrantyService warrantyService;

    @PostMapping
    @PreAuthorize("hasAuthority('WARRANTY_CREATE')")
    @Operation(summary = "Create new warranty")
    public ResponseEntity<ResponseData<WarrantyResponse>> createWarranty(
            @Valid @RequestBody WarrantyRequest request) {
        WarrantyResponse response = warrantyService.createWarranty(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<WarrantyResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Warranty created successfully")
                        .data(response)
                        .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('WARRANTY_READ')")
    @Operation(summary = "Get warranty by ID")
    public ResponseEntity<ResponseData<WarrantyResponse>> getWarrantyById(@PathVariable Long id) {
        WarrantyResponse response = warrantyService.getWarrantyById(id);
        return ResponseEntity.ok(ResponseData.<WarrantyResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Warranty retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/code/{warrantyCode}")
    @PreAuthorize("hasAuthority('WARRANTY_READ')")
    @Operation(summary = "Get warranty by warranty code")
    public ResponseEntity<ResponseData<WarrantyResponse>> getWarrantyByCode(@PathVariable String warrantyCode) {
        WarrantyResponse response = warrantyService.getWarrantyByCode(warrantyCode);
        return ResponseEntity.ok(ResponseData.<WarrantyResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Warranty retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('WARRANTY_READ')")
    @Operation(summary = "Get all warranties with pagination")
    public ResponseEntity<ResponseData<Page<WarrantyResponse>>> getAllWarranties(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<WarrantyResponse> response = warrantyService.getAllWarranties(pageable);
        return ResponseEntity.ok(ResponseData.<Page<WarrantyResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Warranties retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAuthority('WARRANTY_READ')")
    @Operation(summary = "Get warranties by customer")
    public ResponseEntity<ResponseData<List<WarrantyResponse>>> getWarrantiesByCustomer(
            @PathVariable Long customerId) {
        List<WarrantyResponse> response = warrantyService.getWarrantiesByCustomer(customerId);
        return ResponseEntity.ok(ResponseData.<List<WarrantyResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Warranties by customer retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('WARRANTY_READ')")
    @Operation(summary = "Get warranties by status")
    public ResponseEntity<ResponseData<List<WarrantyResponse>>> getWarrantiesByStatus(
            @PathVariable String status) {
        List<WarrantyResponse> response = warrantyService.getWarrantiesByStatus(status);
        return ResponseEntity.ok(ResponseData.<List<WarrantyResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Warranties by status retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/expiring")
    @PreAuthorize("hasAuthority('WARRANTY_READ')")
    @Operation(summary = "Get warranties expiring within days")
    public ResponseEntity<ResponseData<List<WarrantyResponse>>> getWarrantiesExpiringWithinDays(
            @RequestParam(defaultValue = "30") int days) {
        List<WarrantyResponse> response = warrantyService.getWarrantiesExpiringWithinDays(days);
        return ResponseEntity.ok(ResponseData.<List<WarrantyResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Expiring warranties retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/expired")
    @PreAuthorize("hasAuthority('WARRANTY_READ')")
    @Operation(summary = "Get expired warranties")
    public ResponseEntity<ResponseData<List<WarrantyResponse>>> getExpiredWarranties() {
        List<WarrantyResponse> response = warrantyService.getExpiredWarranties();
        return ResponseEntity.ok(ResponseData.<List<WarrantyResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Expired warranties retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('WARRANTY_READ')")
    @Operation(summary = "Get active warranties")
    public ResponseEntity<ResponseData<List<WarrantyResponse>>> getActiveWarranties() {
        List<WarrantyResponse> response = warrantyService.getActiveWarranties();
        return ResponseEntity.ok(ResponseData.<List<WarrantyResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Active warranties retrieved successfully")
                .data(response)
                .build());
    }

    @PutMapping("/{id}/void")
    @PreAuthorize("hasAuthority('WARRANTY_UPDATE')")
    @Operation(summary = "Void warranty")
    public ResponseEntity<ResponseData<Void>> voidWarranty(
            @PathVariable Long id,
            @RequestParam String reason) {
        warrantyService.voidWarranty(id, reason);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Warranty voided successfully")
                .build());
    }

    @PutMapping("/{id}/claim")
    @PreAuthorize("hasAuthority('WARRANTY_UPDATE')")
    @Operation(summary = "Claim warranty")
    public ResponseEntity<ResponseData<Void>> claimWarranty(
            @PathVariable Long id,
            @RequestParam String claimReason) {
        warrantyService.claimWarranty(id, claimReason);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Warranty claimed successfully")
                .build());
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('WARRANTY_READ')")
    @Operation(summary = "Search warranties")
    public ResponseEntity<ResponseData<Page<WarrantyResponse>>> searchWarranties(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<WarrantyResponse> response = warrantyService.searchWarranties(keyword, pageable);
        return ResponseEntity.ok(ResponseData.<Page<WarrantyResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Warranties searched successfully")
                .data(response)
                .build());
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAuthority('WARRANTY_READ')")
    @Operation(summary = "Get warranties by date range")
    public ResponseEntity<ResponseData<List<WarrantyResponse>>> getWarrantiesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<WarrantyResponse> response = warrantyService.getWarrantiesByDateRange(startDate, endDate);
        return ResponseEntity.ok(ResponseData.<List<WarrantyResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Warranties by date range retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    @Operation(summary = "Get warranty statistics")
    public ResponseEntity<ResponseData<WarrantyService.WarrantyStatistics>> getWarrantyStatistics() {
        WarrantyService.WarrantyStatistics stats = warrantyService.getWarrantyStatistics();
        return ResponseEntity.ok(ResponseData.<WarrantyService.WarrantyStatistics>builder()
                .status(HttpStatus.OK.value())
                .message("Warranty statistics retrieved successfully")
                .data(stats)
                .build());
    }
}
