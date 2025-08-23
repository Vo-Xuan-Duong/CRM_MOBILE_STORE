package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.dtos.product.ProductModelRequest;
import com.example.Backend.dtos.product.ProductModelResponse;
import com.example.Backend.models.ProductModel;
import com.example.Backend.services.ProductModelService;
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
@RequestMapping("/api/product-models")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Product Model Management", description = "API quản lý mẫu sản phẩm")
public class ProductModelController {

    private final ProductModelService productModelService;

    @PostMapping
    @Operation(summary = "Tạo mẫu sản phẩm mới", description = "Tạo một mẫu sản phẩm mới trong hệ thống")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<ResponseData<ProductModelResponse>> createProductModel(@Valid @RequestBody ProductModelRequest productModel) {
        try {
            log.info("Tạo mẫu sản phẩm mới: {}", productModel.getName());
            ProductModelResponse createdProductModel = productModelService.createProductModel(productModel);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseData.<ProductModelResponse>builder()
                            .status(HttpStatus.CREATED.value())
                            .message("Tạo mẫu sản phẩm thành công")
                            .data(createdProductModel)
                            .build());
        } catch (Exception e) {
            log.error("Lỗi tạo mẫu sản phẩm: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<ProductModelResponse>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi tạo mẫu sản phẩm: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật mẫu sản phẩm", description = "Cập nhật thông tin mẫu sản phẩm theo ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<ResponseData<ProductModelResponse>> updateProductModel(
            @Parameter(description = "ID của mẫu sản phẩm") @PathVariable @Min(1) Long id,
            @Valid @RequestBody ProductModelRequest productModel) {
        try {
            log.info("Cập nhật mẫu sản phẩm ID: {}", id);
            ProductModelResponse updatedProductModel = productModelService.updateProductModel(id, productModel);
            return ResponseEntity.ok(ResponseData.<ProductModelResponse>builder()
                    .status(HttpStatus.OK.value())
                    .message("Cập nhật mẫu sản phẩm thành công")
                    .data(updatedProductModel)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi cập nhật mẫu sản phẩm ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<ProductModelResponse>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi cập nhật mẫu sản phẩm: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin mẫu sản phẩm theo ID", description = "Lấy chi tiết thông tin một mẫu sản phẩm")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ResponseData<ProductModelResponse>> getProductModelById(
            @Parameter(description = "ID của mẫu sản phẩm") @PathVariable @Min(1) Long id) {
        try {
            ProductModelResponse productModel = productModelService.getProductModelById(id);
            return ResponseEntity.ok(ResponseData.<ProductModelResponse>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy thông tin mẫu sản phẩm thành công")
                    .data(productModel)
                    .build());
        } catch (Exception e) {
            log.error("Không tìm thấy mẫu sản phẩm ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<ProductModelResponse>builder()
                            .status(HttpStatus.NOT_FOUND.value())
                            .message("Không tìm thấy mẫu sản phẩm: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách mẫu sản phẩm", description = "Lấy danh sách tất cả mẫu sản phẩm với phân trang")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ResponseData<Page<ProductModelResponse>>> getAllProductModels(
            @Parameter(description = "Số trang (bắt đầu từ 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sắp xếp theo trường") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<ProductModelResponse> productModels = productModelService.getAllProductModels(pageable);
            return ResponseEntity.ok(ResponseData.<Page<ProductModelResponse>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy danh sách mẫu sản phẩm thành công")
                    .data(productModels)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi lấy danh sách mẫu sản phẩm: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Page<ProductModelResponse>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi lấy danh sách mẫu sản phẩm: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/brand/{brandId}")
    @Operation(summary = "Lấy mẫu sản phẩm theo thương hiệu", description = "Lấy danh sách mẫu sản phẩm của một thương hiệu")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ResponseData<List<ProductModelResponse>>> getProductModelsByBrand(
            @Parameter(description = "ID thương hiệu") @PathVariable Long brandId) {
        try {
            List<ProductModelResponse> response = productModelService.getProductModelsByBrand(brandId);
            return ResponseEntity.ok(ResponseData.<List<ProductModelResponse>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy mẫu sản phẩm theo thương hiệu thành công")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi lấy mẫu sản phẩm theo thương hiệu: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<List<ProductModelResponse>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi lấy mẫu sản phẩm theo thương hiệu: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Lấy mẫu sản phẩm theo danh mục", description = "Lấy danh sách mẫu sản phẩm theo danh mục")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ResponseData<List<ProductModelResponse>>> getProductModelsByCategory(
            @Parameter(description = "Tên danh mục") @PathVariable String category) {
        try {
            List<ProductModelResponse> response = productModelService.getProductModelsByCategory(category);
            return ResponseEntity.ok(ResponseData.<List<ProductModelResponse>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy mẫu sản phẩm theo danh mục thành công")
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi lấy mẫu sản phẩm theo danh mục: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<List<ProductModelResponse>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi lấy mẫu sản phẩm theo danh mục: " + e.getMessage())
                            .build());
        }
    }

    // ==================== DELETE, ACTIVATE, DEACTIVATE APIs ====================

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa mẫu sản phẩm", description = "Xóa vĩnh viễn mẫu sản phẩm khỏi hệ thống")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Void>> deleteProductModel(
            @Parameter(description = "ID của mẫu sản phẩm") @PathVariable @Min(1) Long id) {
        try {
            log.info("Xóa mẫu sản phẩm ID: {}", id);
            productModelService.deleteProductModel(id);
            return ResponseEntity.ok(ResponseData.<Void>builder()
                    .status(HttpStatus.OK.value())
                    .message("Xóa mẫu sản phẩm thành công")
                    .build());
        } catch (Exception e) {
            log.error("Lỗi xóa mẫu sản phẩm ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Void>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi xóa mẫu sản phẩm: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/{id}/activate")
    @Operation(summary = "Kích hoạt mẫu sản phẩm", description = "Kích hoạt mẫu sản phẩm đã bị vô hiệu hóa")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<ResponseData<Void>> activateProductModel(
            @Parameter(description = "ID của mẫu sản phẩm") @PathVariable @Min(1) Long id) {
        try {
            log.info("Kích hoạt mẫu sản phẩm ID: {}", id);
            productModelService.activateProductModel(id);
            return ResponseEntity.ok(ResponseData.<Void>builder()
                    .status(HttpStatus.OK.value())
                    .message("Kích hoạt mẫu sản phẩm thành công")
                    .build());
        } catch (Exception e) {
            log.error("Lỗi kích hoạt mẫu sản phẩm ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Void>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi kích hoạt mẫu sản phẩm: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Vô hiệu hóa mẫu sản phẩm", description = "Vô hiệu hóa mẫu sản phẩm (soft delete)")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<ResponseData<Void>> deactivateProductModel(
            @Parameter(description = "ID của mẫu sản phẩm") @PathVariable @Min(1) Long id) {
        try {
            log.info("Vô hiệu hóa mẫu sản phẩm ID: {}", id);
            productModelService.deactivateProductModel(id);
            return ResponseEntity.ok(ResponseData.<Void>builder()
                    .status(HttpStatus.OK.value())
                    .message("Vô hiệu hóa mẫu sản phẩm thành công")
                    .build());
        } catch (Exception e) {
            log.error("Lỗi vô hiệu hóa mẫu sản phẩm ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Void>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi vô hiệu hóa mẫu sản phẩm: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/active")
    @Operation(summary = "Lấy danh sách mẫu sản phẩm đang hoạt động", description = "Lấy danh sách tất cả mẫu sản phẩm đang được kích hoạt")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ResponseData<List<ProductModel>>> getActiveProductModels() {
        try {
            List<ProductModel> activeProductModels = productModelService.getActiveProductModels();
            return ResponseEntity.ok(ResponseData.<List<ProductModel>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy danh sách mẫu sản phẩm hoạt động thành công")
                    .data(activeProductModels)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi lấy danh sách mẫu sản phẩm hoạt động: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<List<ProductModel>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi lấy danh sách mẫu sản phẩm hoạt động: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/inactive")
    @Operation(summary = "Lấy danh sách mẫu sản phẩm bị vô hiệu hóa", description = "Lấy danh sách tất cả mẫu sản phẩm đã bị vô hiệu hóa")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<List<ProductModel>>> getInactiveProductModels() {
        try {
            List<ProductModel> inactiveProductModels = productModelService.getInactiveProductModels();
            return ResponseEntity.ok(ResponseData.<List<ProductModel>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy danh sách mẫu sản phẩm vô hiệu hóa thành công")
                    .data(inactiveProductModels)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi lấy danh sách mẫu sản phẩm vô hiệu hóa: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<List<ProductModel>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi lấy danh sách mẫu sản phẩm vô hiệu hóa: " + e.getMessage())
                            .build());
        }
    }
}
