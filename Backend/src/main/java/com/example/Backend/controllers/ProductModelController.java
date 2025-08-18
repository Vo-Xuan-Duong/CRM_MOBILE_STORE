package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.dtos.product.ProductModelRequest;
import com.example.Backend.dtos.product.ProductModelResponse;
import com.example.Backend.services.ProductModelService;
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
@RequestMapping("/api/v1/product-models")
@RequiredArgsConstructor
@Tag(name = "Product Model", description = "Product Model Management API")
public class ProductModelController {

    private final ProductModelService productModelService;

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT_CREATE')")
    @Operation(summary = "Create new product model")
    public ResponseEntity<ResponseData<ProductModelResponse>> createProductModel(
            @Valid @RequestBody ProductModelRequest request) {
        ProductModelResponse response = productModelService.createProductModel(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<ProductModelResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Product model created successfully")
                        .data(response)
                        .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    @Operation(summary = "Update product model")
    public ResponseEntity<ResponseData<ProductModelResponse>> updateProductModel(
            @PathVariable Long id,
            @Valid @RequestBody ProductModelRequest request) {
        ProductModelResponse response = productModelService.updateProductModel(id, request);
        return ResponseEntity.ok(ResponseData.<ProductModelResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Product model updated successfully")
                .data(response)
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @Operation(summary = "Get product model by ID")
    public ResponseEntity<ResponseData<ProductModelResponse>> getProductModelById(@PathVariable Long id) {
        ProductModelResponse response = productModelService.getProductModelById(id);
        return ResponseEntity.ok(ResponseData.<ProductModelResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Product model retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @Operation(summary = "Get all product models with pagination")
    public ResponseEntity<ResponseData<Page<ProductModelResponse>>> getAllProductModels(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ProductModelResponse> response = productModelService.getAllProductModels(pageable);
        return ResponseEntity.ok(ResponseData.<Page<ProductModelResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Product models retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @Operation(summary = "Search product models")
    public ResponseEntity<ResponseData<Page<ProductModelResponse>>> searchProductModels(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ProductModelResponse> response = productModelService.searchProductModels(keyword, pageable);
        return ResponseEntity.ok(ResponseData.<Page<ProductModelResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Product models searched successfully")
                .data(response)
                .build());
    }

    @GetMapping("/brand/{brandId}")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @Operation(summary = "Get product models by brand")
    public ResponseEntity<ResponseData<List<ProductModelResponse>>> getProductModelsByBrand(
            @PathVariable Long brandId) {
        List<ProductModelResponse> response = productModelService.getProductModelsByBrand(brandId);
        return ResponseEntity.ok(ResponseData.<List<ProductModelResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Product models by brand retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/category/{category}")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @Operation(summary = "Get product models by category")
    public ResponseEntity<ResponseData<List<ProductModelResponse>>> getProductModelsByCategory(
            @PathVariable String category) {
        List<ProductModelResponse> response = productModelService.getProductModelsByCategory(category);
        return ResponseEntity.ok(ResponseData.<List<ProductModelResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Product models by category retrieved successfully")
                .data(response)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_DELETE')")
    @Operation(summary = "Deactivate product model")
    public ResponseEntity<ResponseData<Void>> deactivateProductModel(@PathVariable Long id) {
        productModelService.deactivateProductModel(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Product model deactivated successfully")
                .build());
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    @Operation(summary = "Activate product model")
    public ResponseEntity<ResponseData<Void>> activateProductModel(@PathVariable Long id) {
        productModelService.activateProductModel(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Product model activated successfully")
                .build());
    }
}
