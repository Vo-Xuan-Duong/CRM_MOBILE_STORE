package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.models.Brand;
import com.example.Backend.services.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Brand Management", description = "API quản lý thương hiệu điện thoại")
public class BrandController {
    
    private final BrandService brandService;
    
    @PostMapping
    @Operation(summary = "Tạo thương hiệu mới", description = "Tạo một thương hiệu điện thoại mới trong hệ thống")
    public ResponseEntity<ResponseData<Brand>> createBrand(@Valid @RequestBody Brand brand) {
        try {
            log.info("Tạo thương hiệu mới: {}", brand.getName());
            Brand createdBrand = brandService.createBrand(brand);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseData.<Brand>builder()
                            .status(HttpStatus.CREATED.value())
                            .message("Tạo thương hiệu thành công")
                            .data(createdBrand)
                            .build());
        } catch (Exception e) {
            log.error("Lỗi tạo thương hiệu: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Brand>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi tạo thương hiệu: " + e.getMessage())
                            .build());
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thương hiệu", description = "Cập nhật thông tin thương hiệu theo ID")
    public ResponseEntity<ResponseData<Brand>> updateBrand(
            @Parameter(description = "ID của thương hiệu") @PathVariable @Min(1) Long id,
            @Valid @RequestBody Brand brand) {
        try {
            log.info("Cập nhật thương hiệu ID: {}", id);
            Brand updatedBrand = brandService.updateBrand(id, brand);
            return ResponseEntity.ok(ResponseData.<Brand>builder()
                    .status(HttpStatus.OK.value())
                    .message("Cập nhật thương hiệu thành công")
                    .data(updatedBrand)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi cập nhật thương hiệu ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Brand>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi cập nhật thương hiệu: " + e.getMessage())
                            .build());
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin thương hiệu theo ID", description = "Lấy chi tiết thông tin một thương hiệu")
    public ResponseEntity<ResponseData<Brand>> getBrandById(
            @Parameter(description = "ID của thương hiệu") @PathVariable @Min(1) Long id) {
        try {
            Brand brand = brandService.getBrandById(id);
            return ResponseEntity.ok(ResponseData.<Brand>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy thông tin thương hiệu thành công")
                    .data(brand)
                    .build());
        } catch (Exception e) {
            log.error("Không tìm thấy thương hiệu ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseData.<Brand>builder()
                            .status(HttpStatus.NOT_FOUND.value())
                            .message("Không tìm thấy thương hiệu: " + e.getMessage())
                            .build());
        }
    }
    
    @GetMapping
    @Operation(summary = "Lấy danh sách thương hiệu có phân trang", description = "Lấy danh sách thương hiệu với phân trang và sắp xếp")
    public ResponseEntity<ResponseData<Page<Brand>>> getAllBrands(
            @Parameter(description = "Số trang (bắt đầu từ 0)") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Số lượng items trên một trang") @RequestParam(defaultValue = "10") @Min(1) int size,
            @Parameter(description = "Trường để sắp xếp") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Hướng sắp xếp (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Page<Brand> brands = brandService.getAllBrands(page, size, sortBy, sortDir);
            return ResponseEntity.ok(ResponseData.<Page<Brand>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy danh sách thương hiệu thành công")
                    .data(brands)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi lấy danh sách thương hiệu: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Page<Brand>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi lấy danh sách thương hiệu: " + e.getMessage())
                            .build());
        }
    }
    
    @GetMapping("/all")
    @Operation(summary = "Lấy tất cả thương hiệu (không phân trang)", description = "Lấy tất cả thương hiệu trong hệ thống")
    public ResponseEntity<ResponseData<List<Brand>>> getAllBrandsNoPaging() {
        try {
            List<Brand> brands = brandService.getAllBrands();
            return ResponseEntity.ok(ResponseData.<List<Brand>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy tất cả thương hiệu thành công")
                    .data(brands)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi lấy tất cả thương hiệu: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<List<Brand>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi lấy danh sách thương hiệu: " + e.getMessage())
                            .build());
        }
    }
    
    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm thương hiệu", description = "Tìm kiếm thương hiệu theo tên hoặc quốc gia")
    public ResponseEntity<ResponseData<Page<Brand>>> searchBrands(
            @Parameter(description = "Từ khóa tìm kiếm") @RequestParam @NotBlank String keyword,
            @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Số lượng items") @RequestParam(defaultValue = "10") @Min(1) int size) {
        try {
            Page<Brand> brands = brandService.searchBrands(keyword, page, size);
            return ResponseEntity.ok(ResponseData.<Page<Brand>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Tìm kiếm thương hiệu thành công")
                    .data(brands)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi tìm kiếm thương hiệu với từ khóa '{}': {}", keyword, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Page<Brand>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi tìm kiếm thương hiệu: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/with-products")
    @Operation(summary = "Lấy thương hiệu có sản phẩm", description = "Lấy danh sách thương hiệu đang có sản phẩm trong hệ thống")
    public ResponseEntity<ResponseData<List<Brand>>> getBrandsWithProducts() {
        try {
            List<Brand> brands = brandService.getBrandsWithProducts();
            return ResponseEntity.ok(ResponseData.<List<Brand>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy thương hiệu có sản phẩm thành công")
                    .data(brands)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi lấy thương hiệu có sản phẩm: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<List<Brand>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi lấy thương hiệu có sản phẩm: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/top/{limit}")
    @Operation(summary = "Lấy thương hiệu phổ biến", description = "Lấy danh sách thương hiệu phổ biến nhất theo số lượng sản phẩm")
    public ResponseEntity<ResponseData<List<Brand>>> getTopBrands(
            @Parameter(description = "Số lượng thương hiệu muốn lấy") @PathVariable @Min(1) int limit) {
        try {
            List<Brand> brands = brandService.getTopBrands(limit);
            return ResponseEntity.ok(ResponseData.<List<Brand>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy thương hiệu phổ biến thành công")
                    .data(brands)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi lấy top thương hiệu: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<List<Brand>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi lấy thương hiệu phổ biến: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/by-country/{country}")
    @Operation(summary = "Lấy thương hiệu theo quốc gia", description = "Lấy danh sách thương hiệu của một quốc gia cụ thể")
    public ResponseEntity<ResponseData<List<Brand>>> getBrandsByCountry(
            @Parameter(description = "Tên quốc gia") @PathVariable @NotBlank String country) {
        try {
            List<Brand> brands = brandService.getBrandsByCountry(country);
            return ResponseEntity.ok(ResponseData.<List<Brand>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy thương hiệu theo quốc gia thành công")
                    .data(brands)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi lấy thương hiệu theo quốc gia '{}': {}", country, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<List<Brand>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi lấy thương hiệu theo quốc gia: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/stats")
    @Operation(summary = "Thống kê thương hiệu", description = "Lấy thống kê tổng quan về thương hiệu")
    public ResponseEntity<ResponseData<Object>> getBrandStats() {
        try {
            long totalBrands = brandService.getTotalBrands();
            long brandsWithProducts = brandService.getBrandsWithProductsCount();

            var stats = new Object() {
                public final long totalBrands = brandService.getTotalBrands();
                public final long brandsWithProducts = brandService.getBrandsWithProductsCount();
                public final long brandsWithoutProducts = totalBrands - brandsWithProducts;
            };

            return ResponseEntity.ok(ResponseData.builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy thống kê thương hiệu thành công")
                    .data(stats)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi lấy thống kê thương hiệu: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi lấy thống kê thương hiệu: " + e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa thương hiệu", description = "Xóa thương hiệu khỏi hệ thống (chỉ xóa được khi không có sản phẩm liên kết)")
    public ResponseEntity<ResponseData<Void>> deleteBrand(
            @Parameter(description = "ID của thương hiệu") @PathVariable @Min(1) Long id) {
        try {
            log.info("Xóa thương hiệu ID: {}", id);
            brandService.deleteBrand(id);
            return ResponseEntity.ok(ResponseData.<Void>builder()
                    .status(HttpStatus.OK.value())
                    .message("Xóa thương hiệu thành công")
                    .build());
        } catch (Exception e) {
            log.error("Lỗi xóa thương hiệu ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Void>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi xóa thương hiệu: " + e.getMessage())
                            .build());
        }
    }
}
