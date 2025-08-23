package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.dtos.stock.StockItemResponse;
import com.example.Backend.dtos.stock.StockMovementRequest;
import com.example.Backend.dtos.stock.StockMovementResponse;
import com.example.Backend.services.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Stock Management", description = "API quản lý kho hàng và tồn kho")
public class StockController {

    private final StockService stockService;

    @GetMapping("/sku/{skuId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Lấy thông tin tồn kho theo SKU ID", description = "Lấy thông tin chi tiết về tồn kho của một SKU")
    public ResponseEntity<ResponseData<StockItemResponse>> getStockBySkuId(
            @Parameter(description = "SKU ID") @PathVariable Long skuId) {
        try {
            log.info("Getting stock for SKU ID: {}", skuId);
            StockItemResponse response = stockService.getStockBySkuId(skuId);
            return ResponseEntity.ok(ResponseData.<StockItemResponse>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy thông tin tồn kho thành công")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error getting stock for SKU ID {}: {}", skuId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<StockItemResponse>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi lấy thông tin tồn kho: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Lấy tất cả thông tin tồn kho", description = "Lấy danh sách tất cả các item trong kho")
    public ResponseEntity<ResponseData<List<StockItemResponse>>> getAllStockItems() {
        try {
            log.info("Getting all stock items");
            List<StockItemResponse> response = stockService.getAllStockItems();
            return ResponseEntity.ok(ResponseData.<List<StockItemResponse>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy danh sách tồn kho thành công")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error getting all stock items: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<List<StockItemResponse>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi lấy danh sách tồn kho: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Lấy danh sách hàng sắp hết", description = "Lấy danh sách các item có tồn kho thấp")
    public ResponseEntity<ResponseData<List<StockItemResponse>>> getLowStockItems() {
        try {
            log.info("Getting low stock items");
            List<StockItemResponse> response = stockService.getLowStockItems();
            return ResponseEntity.ok(ResponseData.<List<StockItemResponse>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy danh sách hàng sắp hết thành công")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error getting low stock items: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<List<StockItemResponse>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi lấy danh sách hàng sắp hết: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/out-of-stock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Lấy danh sách hàng hết hàng", description = "Lấy danh sách các item đã hết hàng")
    public ResponseEntity<ResponseData<List<StockItemResponse>>> getOutOfStockItems() {
        try {
            log.info("Getting out of stock items");
            List<StockItemResponse> response = stockService.getOutOfStockItems();
            return ResponseEntity.ok(ResponseData.<List<StockItemResponse>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy danh sách hàng hết hàng thành công")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Error getting out of stock items: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<List<StockItemResponse>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi lấy danh sách hàng hết hàng: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Lấy danh sách tồn kho với phân trang", description = "Lấy danh sách tồn kho có phân trang và sắp xếp")
    public ResponseEntity<ResponseData<List<StockItemResponse>>> getStockItemsWithPagination(
            @Parameter(description = "Số trang (bắt đầu từ 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sắp xếp theo trường") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            log.info("Getting stock items with basic pagination simulation");

            // Use existing method as temporary solution until proper pagination is implemented
            List<StockItemResponse> allItems = stockService.getAllStockItems();

            // Basic manual pagination (temporary solution)
            int start = page * size;
            int end = Math.min(start + size, allItems.size());
            List<StockItemResponse> paginatedItems = start < allItems.size() ?
                allItems.subList(start, end) : List.of();

            return ResponseEntity.ok(ResponseData.<List<StockItemResponse>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy danh sách tồn kho thành công (pagination cơ bản)")
                    .data(paginatedItems)
                    .build());
        } catch (Exception e) {
            log.error("Error getting stock items with pagination: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<List<StockItemResponse>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi lấy danh sách tồn kho: " + e.getMessage())
                            .build());
        }
    }

    // ==================== STOCK MOVEMENT ENDPOINTS ====================

    @PostMapping("/movements")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WAREHOUSE_MANAGER')")
    @Operation(summary = "Tạo chuyển động kho", description = "Tạo một giao dịch nhập/xuất kho")
    public ResponseEntity<ResponseData<String>> createStockMovement(
            @Valid @RequestBody StockMovementRequest request,
            Authentication authentication) {
        try {
            log.info("Creating stock movement for SKU ID: {}", request.getSkuId());
            // TODO: Implement stockService.createStockMovement() method
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ResponseData.<String>builder()
                            .status(HttpStatus.NOT_IMPLEMENTED.value())
                            .message("Chức năng tạo chuyển động kho chưa được implement")
                            .data("Vui lòng implement StockService.createStockMovement() method")
                            .build());
        } catch (Exception e) {
            log.error("Error creating stock movement: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<String>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi tạo chuyển động kho: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/movements")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Lấy lịch sử chuyển động kho", description = "Lấy danh sách các giao dịch nhập/xuất kho")
    public ResponseEntity<ResponseData<String>> getStockMovements(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            log.info("Getting stock movements with pagination");
            // TODO: Implement stockService.getStockMovements() method
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(ResponseData.<String>builder()
                            .status(HttpStatus.NOT_IMPLEMENTED.value())
                            .message("Chức năng lấy lịch sử chuyển động kho chưa được implement")
                            .data("Vui lòng implement StockService.getStockMovements() method")
                            .build());
        } catch (Exception e) {
            log.error("Error getting stock movements: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<String>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi lấy lịch sử chuyển động kho: " + e.getMessage())
                            .build());
        }
    }

    // ==================== STOCK STATISTICS ====================

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lấy thống kê tồn kho", description = "Lấy các số liệu thống kê về tồn kho")
    public ResponseEntity<ResponseData<Object>> getStockStatistics() {
        try {
            log.info("Getting stock statistics");
            // Basic statistics using available methods
            List<StockItemResponse> allItems = stockService.getAllStockItems();
            List<StockItemResponse> lowStock = stockService.getLowStockItems();
            List<StockItemResponse> outOfStock = stockService.getOutOfStockItems();

            var statistics = java.util.Map.of(
                "totalItems", allItems.size(),
                "lowStockItems", lowStock.size(),
                "outOfStockItems", outOfStock.size(),
                "inStockItems", allItems.size() - outOfStock.size()
            );

            return ResponseEntity.ok(ResponseData.<Object>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy thống kê tồn kho thành công")
                    .data(statistics)
                    .build());
        } catch (Exception e) {
            log.error("Error getting stock statistics: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Object>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi lấy thống kê tồn kho: " + e.getMessage())
                            .build());
        }
    }
}
