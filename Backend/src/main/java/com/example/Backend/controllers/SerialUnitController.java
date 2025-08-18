package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.dtos.serial.SerialUnitRequest;
import com.example.Backend.dtos.serial.SerialUnitResponse;
import com.example.Backend.services.SerialUnitService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/serial-units")
@RequiredArgsConstructor
@Tag(name = "Serial Unit", description = "Serial Unit Management API")
public class SerialUnitController {

    private final SerialUnitService serialUnitService;

    @PostMapping
    @PreAuthorize("hasAuthority('INVENTORY_UPDATE')")
    @Operation(summary = "Create new serial unit")
    public ResponseEntity<ResponseData<SerialUnitResponse>> createSerialUnit(
            @Valid @RequestBody SerialUnitRequest request) {
        SerialUnitResponse response = serialUnitService.createSerialUnit(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<SerialUnitResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Serial unit created successfully")
                        .data(response)
                        .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('INVENTORY_READ')")
    @Operation(summary = "Get serial unit by ID")
    public ResponseEntity<ResponseData<SerialUnitResponse>> getSerialUnitById(@PathVariable Long id) {
        SerialUnitResponse response = serialUnitService.getSerialUnitById(id);
        return ResponseEntity.ok(ResponseData.<SerialUnitResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Serial unit retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/imei/{imei}")
    @PreAuthorize("hasAuthority('INVENTORY_READ')")
    @Operation(summary = "Get serial unit by IMEI")
    public ResponseEntity<ResponseData<SerialUnitResponse>> getSerialUnitByImei(@PathVariable String imei) {
        SerialUnitResponse response = serialUnitService.getSerialUnitByImei(imei);
        return ResponseEntity.ok(ResponseData.<SerialUnitResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Serial unit retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/sku/{skuId}")
    @PreAuthorize("hasAuthority('INVENTORY_READ')")
    @Operation(summary = "Get serial units by SKU")
    public ResponseEntity<ResponseData<List<SerialUnitResponse>>> getSerialUnitsBySkuId(@PathVariable Long skuId) {
        List<SerialUnitResponse> response = serialUnitService.getSerialUnitsBySkuId(skuId);
        return ResponseEntity.ok(ResponseData.<List<SerialUnitResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Serial units by SKU retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('INVENTORY_READ')")
    @Operation(summary = "Get serial units by status")
    public ResponseEntity<ResponseData<List<SerialUnitResponse>>> getSerialUnitsByStatus(@PathVariable String status) {
        List<SerialUnitResponse> response = serialUnitService.getSerialUnitsByStatus(status);
        return ResponseEntity.ok(ResponseData.<List<SerialUnitResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Serial units by status retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/available")
    @PreAuthorize("hasAuthority('INVENTORY_READ')")
    @Operation(summary = "Get available serial units")
    public ResponseEntity<ResponseData<List<SerialUnitResponse>>> getAvailableSerialUnits() {
        List<SerialUnitResponse> response = serialUnitService.getAvailableSerialUnits();
        return ResponseEntity.ok(ResponseData.<List<SerialUnitResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Available serial units retrieved successfully")
                .data(response)
                .build());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('INVENTORY_UPDATE')")
    @Operation(summary = "Update serial unit status")
    public ResponseEntity<ResponseData<Void>> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        serialUnitService.updateStatus(id, status);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Serial unit status updated successfully")
                .build());
    }

    @PutMapping("/{id}/sell")
    @PreAuthorize("hasAuthority('SALES_CREATE')")
    @Operation(summary = "Mark serial unit as sold")
    public ResponseEntity<ResponseData<Void>> markAsSold(@PathVariable Long id) {
        serialUnitService.markAsSold(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Serial unit marked as sold")
                .build());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('INVENTORY_READ')")
    @Operation(summary = "Get all serial units with pagination")
    public ResponseEntity<ResponseData<Page<SerialUnitResponse>>> getAllSerialUnits(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<SerialUnitResponse> response = serialUnitService.getAllSerialUnits(pageable);
        return ResponseEntity.ok(ResponseData.<Page<SerialUnitResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Serial units retrieved successfully")
                .data(response)
                .build());
    }
}
