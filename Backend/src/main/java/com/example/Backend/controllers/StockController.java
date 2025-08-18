package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.dtos.stock.StockItemResponse;
import com.example.Backend.dtos.stock.StockMovementRequest;
import com.example.Backend.dtos.stock.StockMovementResponse;
import com.example.Backend.services.StockService;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
@Tag(name = "Stock", description = "Stock Management API")
public class StockController {

    private final StockService stockService;

    @GetMapping("/sku/{skuId}")
    @PreAuthorize("hasAuthority('INVENTORY_READ')")
    @Operation(summary = "Get stock by SKU ID")
    public ResponseEntity<ResponseData<StockItemResponse>> getStockBySkuId(@PathVariable Long skuId) {
        StockItemResponse response = stockService.getStockBySkuId(skuId);
        return ResponseEntity.ok(ResponseData.<StockItemResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Stock retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('INVENTORY_READ')")
    @Operation(summary = "Get all stock items")
    public ResponseEntity<ResponseData<List<StockItemResponse>>> getAllStockItems() {
        List<StockItemResponse> response = stockService.getAllStockItems();
        return ResponseEntity.ok(ResponseData.<List<StockItemResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("All stock items retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAuthority('INVENTORY_READ')")
    @Operation(summary = "Get low stock items")
    public ResponseEntity<ResponseData<List<StockItemResponse>>> getLowStockItems() {
        List<StockItemResponse> response = stockService.getLowStockItems();
        return ResponseEntity.ok(ResponseData.<List<StockItemResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Low stock items retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/out-of-stock")
    @PreAuthorize("hasAuthority('INVENTORY_READ')")
    @Operation(summary = "Get out of stock items")
    public ResponseEntity<ResponseData<List<StockItemResponse>>> getOutOfStockItems() {
        List<StockItemResponse> response = stockService.getOutOfStockItems();
        return ResponseEntity.ok(ResponseData.<List<StockItemResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Out of stock items retrieved successfully")
                .data(response)
                .build());
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('INVENTORY_UPDATE')")
    @Operation(summary = "Add stock")
    public ResponseEntity<ResponseData<StockMovementResponse>> addStock(
            @Valid @RequestBody StockMovementRequest request,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        StockMovementResponse response = stockService.addStock(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<StockMovementResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Stock added successfully")
                        .data(response)
                        .build());
    }

    @PostMapping("/remove")
    @PreAuthorize("hasAuthority('INVENTORY_UPDATE')")
    @Operation(summary = "Remove stock")
    public ResponseEntity<ResponseData<StockMovementResponse>> removeStock(
            @Valid @RequestBody StockMovementRequest request,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        StockMovementResponse response = stockService.removeStock(request, userId);
        return ResponseEntity.ok(ResponseData.<StockMovementResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Stock removed successfully")
                .data(response)
                .build());
    }

    @PutMapping("/adjust/{skuId}")
    @PreAuthorize("hasAuthority('INVENTORY_UPDATE')")
    @Operation(summary = "Adjust stock")
    public ResponseEntity<ResponseData<StockItemResponse>> adjustStock(
            @PathVariable Long skuId,
            @RequestParam Integer newQuantity,
            @RequestParam String reason,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        StockItemResponse response = stockService.adjustStock(skuId, newQuantity, reason, userId);
        return ResponseEntity.ok(ResponseData.<StockItemResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Stock adjusted successfully")
                .data(response)
                .build());
    }

    @PostMapping("/reserve")
    @PreAuthorize("hasAuthority('INVENTORY_UPDATE')")
    @Operation(summary = "Reserve stock")
    public ResponseEntity<ResponseData<Boolean>> reserveStock(
            @RequestParam Long skuId,
            @RequestParam Integer quantity) {
        boolean success = stockService.reserveStock(skuId, quantity);
        return ResponseEntity.ok(ResponseData.<Boolean>builder()
                .status(HttpStatus.OK.value())
                .message(success ? "Stock reserved successfully" : "Failed to reserve stock")
                .data(success)
                .build());
    }

    @PostMapping("/release-reservation")
    @PreAuthorize("hasAuthority('INVENTORY_UPDATE')")
    @Operation(summary = "Release stock reservation")
    public ResponseEntity<ResponseData<Boolean>> releaseReservation(
            @RequestParam Long skuId,
            @RequestParam Integer quantity) {
        boolean success = stockService.releaseReservation(skuId, quantity);
        return ResponseEntity.ok(ResponseData.<Boolean>builder()
                .status(HttpStatus.OK.value())
                .message(success ? "Reservation released successfully" : "Failed to release reservation")
                .data(success)
                .build());
    }

    @GetMapping("/movements")
    @PreAuthorize("hasAuthority('INVENTORY_READ')")
    @Operation(summary = "Get stock movements")
    public ResponseEntity<ResponseData<Page<StockMovementResponse>>> getStockMovements(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<StockMovementResponse> response = stockService.getStockMovements(pageable);
        return ResponseEntity.ok(ResponseData.<Page<StockMovementResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Stock movements retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/movements/sku/{skuId}")
    @PreAuthorize("hasAuthority('INVENTORY_READ')")
    @Operation(summary = "Get stock movements by SKU")
    public ResponseEntity<ResponseData<List<StockMovementResponse>>> getStockMovementsBySkuId(
            @PathVariable Long skuId) {
        List<StockMovementResponse> response = stockService.getStockMovementsBySkuId(skuId);
        return ResponseEntity.ok(ResponseData.<List<StockMovementResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Stock movements by SKU retrieved successfully")
                .data(response)
                .build());
    }

    @PutMapping("/levels/{skuId}")
    @PreAuthorize("hasAuthority('INVENTORY_UPDATE')")
    @Operation(summary = "Update stock levels")
    public ResponseEntity<ResponseData<Void>> updateStockLevels(
            @PathVariable Long skuId,
            @RequestParam Integer minStock,
            @RequestParam(required = false) Integer maxStock) {
        stockService.updateStockLevels(skuId, minStock, maxStock);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Stock levels updated successfully")
                .build());
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('INVENTORY_READ')")
    @Operation(summary = "Get stock statistics")
    public ResponseEntity<ResponseData<StockStatistics>> getStockStatistics() {
        StockStatistics stats = StockStatistics.builder()
                .totalStockValue(stockService.getTotalStockValue())
                .totalStockQuantity(stockService.getTotalStockQuantity())
                .totalReservedQuantity(stockService.getTotalReservedQuantity())
                .lowStockItemCount(stockService.getLowStockItemCount())
                .build();

        return ResponseEntity.ok(ResponseData.<StockStatistics>builder()
                .status(HttpStatus.OK.value())
                .message("Stock statistics retrieved successfully")
                .data(stats)
                .build());
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        // Implementation to extract user ID from authentication
        return 1L; // Placeholder
    }

    @lombok.Data
    @lombok.Builder
    public static class StockStatistics {
        private Long totalStockValue;
        private Long totalStockQuantity;
        private Long totalReservedQuantity;
        private long lowStockItemCount;
    }
}
