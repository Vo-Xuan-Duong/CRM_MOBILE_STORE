package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.dtos.product.SKURequest;
import com.example.Backend.dtos.product.SKUResponse;
import com.example.Backend.services.SKUService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skus")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "SKU Management", description = "API quản lý SKU sản phẩm")
public class SKUController {

    private final SKUService skuService;

    @PostMapping
    @Operation(summary = "Tạo SKU mới", description = "Tạo một SKU sản phẩm mới trong hệ thống")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<ResponseData<SKUResponse>> createSKU(@Valid @RequestBody SKURequest sku) {
        try {
            log.info("Tạo SKU mới: {}", sku.getCode());
            SKUResponse createdSKU = skuService.createSKU(sku);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseData.<SKUResponse>builder()
                            .status(HttpStatus.CREATED.value())
                            .message("Tạo SKU thành công")
                            .data(createdSKU)
                            .build());
        } catch (Exception e) {
            log.error("Lỗi tạo SKU: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<SKUResponse>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi tạo SKU: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật SKU", description = "Cập nhật thông tin SKU theo ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<ResponseData<SKUResponse>> updateSKU(
            @Parameter(description = "ID của SKU") @PathVariable @Min(1) Long id,
            @Valid @RequestBody SKURequest sku) {
        try {
            log.info("Cập nhật SKU ID: {}", id);
            SKUResponse updatedSKU = skuService.updateSKU(id, sku);
            return ResponseEntity.ok(ResponseData.<SKUResponse>builder()
                    .status(HttpStatus.OK.value())
                    .message("Cập nhật SKU thành công")
                    .data(updatedSKU)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi cập nhật SKU ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<SKUResponse>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi cập nhật SKU: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin SKU theo ID", description = "Lấy chi tiết thông tin một SKU")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ResponseData<SKUResponse>> getSKUById(
            @Parameter(description = "ID của SKU") @PathVariable @Min(1) Long id) {
        try {
            SKUResponse sku = skuService.getSKUById(id);
            return ResponseEntity.ok(ResponseData.<SKUResponse>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy thông tin SKU thành công")
                    .data(sku)
                    .build());
        } catch (Exception e) {
            log.error("Không tìm thấy SKU ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<SKUResponse>builder()
                            .status(HttpStatus.NOT_FOUND.value())
                            .message("Không tìm thấy SKU: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách SKU", description = "Lấy danh sách tất cả SKU với phân trang")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ResponseData<Page<SKUResponse>>> getAllSKUs(
            @Parameter(description = "Số trang (bắt đầu từ 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sắp xếp theo trường") @RequestParam(defaultValue = "code") String sortBy,
            @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<SKUResponse> skus = skuService.getAllSKUs(pageable);
            return ResponseEntity.ok(ResponseData.<Page<SKUResponse>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy danh sách SKU thành công")
                    .data(skus)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi lấy danh sách SKU: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Page<SKUResponse>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi lấy danh sách SKU: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/product-model/{productModelId}")
    @Operation(summary = "Lấy SKU theo mẫu sản phẩm", description = "Lấy danh sách SKU của một mẫu sản phẩm")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ResponseData<List<SKUResponse>>> getSKUsByProductModel(
            @Parameter(description = "ID mẫu sản phẩm") @PathVariable Long productModelId) {
        try {
            List<SKUResponse> response = skuService.getSKUsByProductModel(productModelId);
            return ResponseEntity.ok(ResponseData.<List<SKUResponse>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy SKU theo mẫu sản phẩm thành công")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi lấy SKU theo mẫu sản phẩm: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<List<SKUResponse>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi lấy SKU theo mẫu sản phẩm: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/barcode/{barcode}")
    @Operation(summary = "Lấy SKU theo mã barcode", description = "Lấy thông tin SKU theo mã barcode")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ResponseData<SKUResponse>> getSKUByBarcode(
            @Parameter(description = "Mã barcode của SKU") @PathVariable String barcode) {
        try {
            SKUResponse sku = skuService.getSKUByBarcode(barcode);
            return ResponseEntity.ok(ResponseData.<SKUResponse>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy SKU theo mã barcode thành công")
                    .data(sku)
                    .build());
        } catch (Exception e) {
            log.error("Không tìm thấy SKU với barcode: {}", barcode);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<SKUResponse>builder()
                            .status(HttpStatus.NOT_FOUND.value())
                            .message("Không tìm thấy SKU với barcode: " + e.getMessage())
                            .build());
        }
    }

    // ==================== ACTIVATE, DEACTIVATE APIs ====================

    @PutMapping("/{id}/activate")
    @Operation(summary = "Kích hoạt SKU", description = "Kích hoạt SKU đã bị vô hiệu hóa")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<ResponseData<Void>> activateSKU(
            @Parameter(description = "ID của SKU") @PathVariable @Min(1) Long id) {
        try {
            log.info("Kích hoạt SKU ID: {}", id);
            skuService.activateSKU(id);
            return ResponseEntity.ok(ResponseData.<Void>builder()
                    .status(HttpStatus.OK.value())
                    .message("Kích hoạt SKU thành công")
                    .build());
        } catch (Exception e) {
            log.error("Lỗi kích hoạt SKU ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Void>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi kích hoạt SKU: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Vô hiệu hóa SKU", description = "Vô hiệu hóa SKU (soft delete)")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<ResponseData<Void>> deactivateSKU(
            @Parameter(description = "ID của SKU") @PathVariable @Min(1) Long id) {
        try {
            log.info("Vô hiệu hóa SKU ID: {}", id);
            skuService.deactivateSKU(id);
            return ResponseEntity.ok(ResponseData.<Void>builder()
                    .status(HttpStatus.OK.value())
                    .message("Vô hiệu hóa SKU thành công")
                    .build());
        } catch (Exception e) {
            log.error("Lỗi vô hiệu hóa SKU ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Void>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi vô hiệu hóa SKU: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm SKU", description = "Tìm kiếm SKU theo từ khóa với phân trang")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ResponseData<Page<SKUResponse>>> searchSKUs(
            @Parameter(description = "Từ khóa tìm kiếm") @RequestParam String keyword,
            @Parameter(description = "Số trang (bắt đầu từ 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sắp xếp theo trường") @RequestParam(defaultValue = "code") String sortBy,
            @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<SKUResponse> skus = skuService.searchSKUs(keyword, pageable);
            return ResponseEntity.ok(ResponseData.<Page<SKUResponse>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Tìm kiếm SKU thành công")
                    .data(skus)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi tìm kiếm SKU: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Page<SKUResponse>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi tìm kiếm SKU: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/brand/{brandId}")
    @Operation(summary = "Lấy SKU theo thương hiệu", description = "Lấy danh sách SKU theo thương hiệu")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ResponseData<List<SKUResponse>>> getSKUsByBrand(
            @Parameter(description = "ID thương hiệu") @PathVariable Long brandId) {
        try {
            List<SKUResponse> response = skuService.getSKUsByBrand(brandId);
            return ResponseEntity.ok(ResponseData.<List<SKUResponse>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy SKU theo thương hiệu thành công")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi lấy SKU theo thương hiệu: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<List<SKUResponse>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi lấy SKU theo thương hiệu: " + e.getMessage())
                            .build());
        }
    }
}
