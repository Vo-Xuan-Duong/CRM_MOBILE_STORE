package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.dtos.serial.SerialUnitRequest;
import com.example.Backend.dtos.serial.SerialUnitResponse;
import com.example.Backend.services.SerialUnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/serial-units")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Serial Unit Management", description = "APIs for managing serial units and IMEI tracking")
public class SerialUnitController {

    private final SerialUnitService serialUnitService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('INVENTORY_MANAGER')")
    @Operation(summary = "Create new serial unit", description = "Create a new serial unit with IMEI tracking")
    public ResponseEntity<ResponseData<SerialUnitResponse>> createSerialUnit(
            @Valid @RequestBody SerialUnitRequest request) {
        try {
            log.info("Creating new serial unit with IMEI: {}", request.getImei());
            SerialUnitResponse response = serialUnitService.createSerialUnit(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseData.<SerialUnitResponse>builder()
                            .status(HttpStatus.CREATED.value())
                            .message("Serial unit created successfully")
                            .data(response)
                            .build());
        } catch (Exception e) {
            log.error("Error creating serial unit: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<SerialUnitResponse>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error creating serial unit: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Get serial unit by ID", description = "Retrieve serial unit information by ID")
    public ResponseEntity<ResponseData<SerialUnitResponse>> getSerialUnitById(
            @Parameter(description = "Serial Unit ID") @PathVariable Long id) {
        try {
            log.info("Getting serial unit with ID: {}", id);
            SerialUnitResponse response = serialUnitService.getSerialUnitById(id);
            return ResponseEntity.ok(ResponseData.<SerialUnitResponse>builder()
                    .status(HttpStatus.OK.value())
                    .message("Serial unit retrieved successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error getting serial unit ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<SerialUnitResponse>builder()
                            .status(HttpStatus.NOT_FOUND.value())
                            .message("Serial unit not found: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/imei/{imei}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Get serial unit by IMEI", description = "Find serial unit by IMEI number")
    public ResponseEntity<ResponseData<SerialUnitResponse>> getSerialUnitByImei(
            @Parameter(description = "IMEI number") @PathVariable String imei) {
        try {
            log.info("Getting serial unit with IMEI: {}", imei);
            SerialUnitResponse response = serialUnitService.getSerialUnitByImei(imei);
            return ResponseEntity.ok(ResponseData.<SerialUnitResponse>builder()
                    .status(HttpStatus.OK.value())
                    .message("Serial unit retrieved successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error getting serial unit by IMEI {}: {}", imei, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<SerialUnitResponse>builder()
                            .status(HttpStatus.NOT_FOUND.value())
                            .message("Serial unit not found: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Get all serial units", description = "Get paginated list of all serial units")
    public ResponseEntity<ResponseData<Page<SerialUnitResponse>>> getAllSerialUnits(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            log.info("Getting all serial units with pagination");
            Page<SerialUnitResponse> response = serialUnitService.getAllSerialUnits(pageable);
            return ResponseEntity.ok(ResponseData.<Page<SerialUnitResponse>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Serial units retrieved successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error getting serial units: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Page<SerialUnitResponse>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error getting serial units: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/sku/{skuId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Get serial units by SKU", description = "Get all serial units for a specific SKU")
    public ResponseEntity<ResponseData<List<SerialUnitResponse>>> getSerialUnitsBySkuId(
            @Parameter(description = "SKU ID") @PathVariable Long skuId) {
        try {
            log.info("Getting serial units for SKU ID: {}", skuId);
            List<SerialUnitResponse> response = serialUnitService.getSerialUnitsBySkuId(skuId);
            return ResponseEntity.ok(ResponseData.<List<SerialUnitResponse>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Serial units retrieved successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error getting serial units for SKU ID {}: {}", skuId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<List<SerialUnitResponse>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error getting serial units: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INVENTORY_MANAGER')")
    @Operation(summary = "Update serial unit", description = "Update serial unit information")
    public ResponseEntity<ResponseData<SerialUnitResponse>> updateSerialUnit(
            @Parameter(description = "Serial Unit ID") @PathVariable Long id,
            @Valid @RequestBody SerialUnitRequest request) {
        try {
            log.info("Updating serial unit ID: {}", id);

            // Note: Currently only status updates are supported via the separate status endpoint
            // This endpoint is prepared for future enhancements
            SerialUnitResponse response = serialUnitService.getSerialUnitById(id);

            return ResponseEntity.ok(ResponseData.<SerialUnitResponse>builder()
                    .status(HttpStatus.OK.value())
                    .message("Serial unit retrieved successfully (update functionality limited)")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error updating serial unit ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<SerialUnitResponse>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error updating serial unit: " + e.getMessage())
                            .build());
        }
    }

    // ==================== STATUS MANAGEMENT ====================

    @PutMapping("/{id}/sold")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Mark serial unit as sold", description = "Mark a serial unit as sold")
    public ResponseEntity<ResponseData<String>> markAsSold(
            @Parameter(description = "Serial Unit ID") @PathVariable Long id) {
        try {
            log.info("Marking serial unit ID {} as sold", id);
            serialUnitService.markAsSold(id);
            return ResponseEntity.ok(ResponseData.<String>builder()
                    .status(HttpStatus.OK.value())
                    .message("Serial unit marked as sold successfully")
                    .data("Serial unit with ID " + id + " has been marked as sold")
                    .build());
        } catch (Exception e) {
            log.error("Error marking serial unit ID {} as sold: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<String>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error marking as sold: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INVENTORY_MANAGER')")
    @Operation(summary = "Update serial unit status", description = "Update the status of a serial unit")
    public ResponseEntity<ResponseData<SerialUnitResponse>> updateStatus(
            @Parameter(description = "Serial Unit ID") @PathVariable Long id,
            @Parameter(description = "New status") @RequestParam String status) {
        try {
            log.info("Updating status for serial unit ID {} to {}", id, status);
            serialUnitService.updateStatus(id, status);
            SerialUnitResponse response = serialUnitService.getSerialUnitById(id);
            return ResponseEntity.ok(ResponseData.<SerialUnitResponse>builder()
                    .status(HttpStatus.OK.value())
                    .message("Serial unit status updated successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error updating status for serial unit ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<SerialUnitResponse>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error updating status: " + e.getMessage())
                            .build());
        }
    }

    // ==================== FILTERING AND SEARCH ====================

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Get serial units by status", description = "Get all serial units with a specific status")
    public ResponseEntity<ResponseData<List<SerialUnitResponse>>> getSerialUnitsByStatus(
            @Parameter(description = "Status (IN_STOCK, SOLD, RETURNED, DEFECTIVE)") @PathVariable String status) {
        try {
            log.info("Getting serial units with status: {}", status);
            List<SerialUnitResponse> response = serialUnitService.getSerialUnitsByStatus(status);
            return ResponseEntity.ok(ResponseData.<List<SerialUnitResponse>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Serial units retrieved successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error getting serial units by status {}: {}", status, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<List<SerialUnitResponse>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error getting serial units: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/available")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Get available serial units", description = "Get all available serial units (IN_STOCK status)")
    public ResponseEntity<ResponseData<List<SerialUnitResponse>>> getAvailableSerialUnits() {
        try {
            log.info("Getting available serial units");
            List<SerialUnitResponse> response = serialUnitService.getAvailableSerialUnits();
            return ResponseEntity.ok(ResponseData.<List<SerialUnitResponse>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Available serial units retrieved successfully")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error getting available serial units: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<List<SerialUnitResponse>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error getting available serial units: " + e.getMessage())
                            .build());
        }
    }
}
