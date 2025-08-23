package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.dtos.brand.BrandCreateDTO;
import com.example.Backend.dtos.brand.BrandResponseDTO;
import com.example.Backend.dtos.brand.BrandUpdateDTO;
import com.example.Backend.models.Brand;
import com.example.Backend.services.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<ResponseData<BrandResponseDTO>> createBrand(@Valid @RequestBody BrandCreateDTO brand) {
        try {
            log.info("Tạo thương hiệu mới: {}", brand.getName());
            BrandResponseDTO createdBrand = brandService.createBrand(brand);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseData.<BrandResponseDTO>builder()
                            .status(HttpStatus.CREATED.value())
                            .message("Tạo thương hiệu thành công")
                            .data(createdBrand)
                            .build());
        } catch (Exception e) {
            log.error("Lỗi tạo thương hiệu: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<BrandResponseDTO>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi tạo thương hiệu: " + e.getMessage())
                            .build());
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thương hiệu", description = "Cập nhật thông tin thương hiệu theo ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<ResponseData<BrandResponseDTO>> updateBrand(
            @Parameter(description = "ID của thương hiệu") @PathVariable @Min(1) Long id,
            @Valid @RequestBody BrandUpdateDTO brand) {
        try {
            log.info("Cập nhật thương hiệu ID: {}", id);
            BrandResponseDTO updatedBrand = brandService.updateBrand(id, brand);
            return ResponseEntity.ok(ResponseData.<BrandResponseDTO>builder()
                    .status(HttpStatus.OK.value())
                    .message("Cập nhật thương hiệu thành công")
                    .data(updatedBrand)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi cập nhật thương hiệu ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<BrandResponseDTO>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi cập nhật thương hiệu: " + e.getMessage())
                            .build());
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin thương hiệu theo ID", description = "Lấy chi tiết thông tin một thương hiệu")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
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
    @Operation(summary = "Lấy danh sách thương hiệu", description = "Lấy danh sách tất cả thương hiệu với phân trang")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ResponseData<Page<Brand>>> getAllBrands(
            @Parameter(description = "Số trang (bắt đầu từ 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sắp xếp theo trường") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Hướng sắp xếp") @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Brand> brands = brandService.getAllBrands(pageable);
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

    // ==================== DELETE, ACTIVATE, DEACTIVATE APIs ====================

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa thương hiệu", description = "Xóa vĩnh viễn thương hiệu khỏi hệ thống")
    @PreAuthorize("hasRole('ADMIN')")
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

    @PutMapping("/{id}/activate")
    @Operation(summary = "Kích hoạt thương hiệu", description = "Kích hoạt thương hiệu đã bị vô hiệu hóa")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<ResponseData<Void>> activateBrand(
            @Parameter(description = "ID của thương hiệu") @PathVariable @Min(1) Long id) {
        try {
            log.info("Kích hoạt thương hiệu ID: {}", id);
            brandService.activateBrand(id);
            return ResponseEntity.ok(ResponseData.<Void>builder()
                    .status(HttpStatus.OK.value())
                    .message("Kích hoạt thương hiệu thành công")
                    .build());
        } catch (Exception e) {
            log.error("Lỗi kích hoạt thương hiệu ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Void>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi kích hoạt thương hiệu: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Vô hiệu hóa thương hiệu", description = "Vô hiệu hóa thương hiệu (soft delete)")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRODUCT_MANAGER')")
    public ResponseEntity<ResponseData<Void>> deactivateBrand(
            @Parameter(description = "ID của thương hiệu") @PathVariable @Min(1) Long id) {
        try {
            log.info("Vô hiệu hóa thương hiệu ID: {}", id);
            brandService.deactivateBrand(id);
            return ResponseEntity.ok(ResponseData.<Void>builder()
                    .status(HttpStatus.OK.value())
                    .message("Vô hiệu hóa thương hiệu thành công")
                    .build());
        } catch (Exception e) {
            log.error("Lỗi vô hiệu hóa thương hiệu ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Void>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi vô hiệu hóa thương hiệu: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/active")
    @Operation(summary = "Lấy danh sách thương hiệu đang hoạt động", description = "Lấy danh sách tất cả thương hiệu đang được kích hoạt")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ResponseData<List<Brand>>> getActiveBrands() {
        try {
            List<Brand> activeBrands = brandService.getActiveBrands();
            return ResponseEntity.ok(ResponseData.<List<Brand>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy danh sách thương hiệu hoạt động thành công")
                    .data(activeBrands)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi lấy danh sách thương hiệu hoạt động: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<List<Brand>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi lấy danh sách thương hiệu hoạt động: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/inactive")
    @Operation(summary = "Lấy danh sách thương hiệu bị vô hiệu hóa", description = "Lấy danh sách tất cả thương hiệu đã bị vô hiệu hóa")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<List<Brand>>> getInactiveBrands() {
        try {
            List<Brand> inactiveBrands = brandService.getInactiveBrands();
            return ResponseEntity.ok(ResponseData.<List<Brand>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy danh sách thương hiệu vô hiệu hóa thành công")
                    .data(inactiveBrands)
                    .build());
        } catch (Exception e) {
            log.error("Lỗi lấy danh sách thương hiệu vô hiệu hóa: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<List<Brand>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Lỗi lấy danh sách thương hiệu vô hiệu hóa: " + e.getMessage())
                            .build());
        }
    }
}
