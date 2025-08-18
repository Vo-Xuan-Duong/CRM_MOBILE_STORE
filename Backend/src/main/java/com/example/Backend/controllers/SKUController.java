package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.dtos.product.SKURequest;
import com.example.Backend.dtos.product.SKUResponse;
import com.example.Backend.services.SKUService;
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

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/skus")
@RequiredArgsConstructor
@Tag(name = "SKU", description = "SKU Management API")
public class SKUController {

    private final SKUService skuService;

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT_CREATE')")
    @Operation(summary = "Create new SKU")
    public ResponseEntity<ResponseData<SKUResponse>> createSKU(
            @Valid @RequestBody SKURequest request) {
        SKUResponse response = skuService.createSKU(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<SKUResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("SKU created successfully")
                        .data(response)
                        .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    @Operation(summary = "Update SKU")
    public ResponseEntity<ResponseData<SKUResponse>> updateSKU(
            @PathVariable Long id,
            @Valid @RequestBody SKURequest request) {
        SKUResponse response = skuService.updateSKU(id, request);
        return ResponseEntity.ok(ResponseData.<SKUResponse>builder()
                .status(HttpStatus.OK.value())
                .message("SKU updated successfully")
                .data(response)
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @Operation(summary = "Get SKU by ID")
    public ResponseEntity<ResponseData<SKUResponse>> getSKUById(@PathVariable Long id) {
        SKUResponse response = skuService.getSKUById(id);
        return ResponseEntity.ok(ResponseData.<SKUResponse>builder()
                .status(HttpStatus.OK.value())
                .message("SKU retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/barcode/{barcode}")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @Operation(summary = "Get SKU by barcode")
    public ResponseEntity<ResponseData<SKUResponse>> getSKUByBarcode(@PathVariable String barcode) {
        SKUResponse response = skuService.getSKUByBarcode(barcode);
        return ResponseEntity.ok(ResponseData.<SKUResponse>builder()
                .status(HttpStatus.OK.value())
                .message("SKU retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @Operation(summary = "Get all SKUs with pagination")
    public ResponseEntity<ResponseData<Page<SKUResponse>>> getAllSKUs(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<SKUResponse> response = skuService.getAllSKUs(pageable);
        return ResponseEntity.ok(ResponseData.<Page<SKUResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("SKUs retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @Operation(summary = "Search SKUs")
    public ResponseEntity<ResponseData<Page<SKUResponse>>> searchSKUs(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<SKUResponse> response = skuService.searchSKUs(keyword, pageable);
        return ResponseEntity.ok(ResponseData.<Page<SKUResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("SKUs searched successfully")
                .data(response)
                .build());
    }

    @GetMapping("/model/{modelId}")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @Operation(summary = "Get SKUs by product model")
    public ResponseEntity<ResponseData<List<SKUResponse>>> getSKUsByProductModel(
            @PathVariable Long modelId) {
        List<SKUResponse> response = skuService.getSKUsByProductModel(modelId);
        return ResponseEntity.ok(ResponseData.<List<SKUResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("SKUs by product model retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/price-range")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @Operation(summary = "Get SKUs by price range")
    public ResponseEntity<ResponseData<List<SKUResponse>>> getSKUsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        List<SKUResponse> response = skuService.getSKUsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(ResponseData.<List<SKUResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("SKUs by price range retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/brand/{brandId}")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @Operation(summary = "Get SKUs by brand")
    public ResponseEntity<ResponseData<List<SKUResponse>>> getSKUsByBrand(
            @PathVariable Long brandId) {
        List<SKUResponse> response = skuService.getSKUsByBrand(brandId);
        return ResponseEntity.ok(ResponseData.<List<SKUResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("SKUs by brand retrieved successfully")
                .data(response)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_DELETE')")
    @Operation(summary = "Deactivate SKU")
    public ResponseEntity<ResponseData<Void>> deactivateSKU(@PathVariable Long id) {
        skuService.deactivateSKU(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("SKU deactivated successfully")
                .build());
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    @Operation(summary = "Activate SKU")
    public ResponseEntity<ResponseData<Void>> activateSKU(@PathVariable Long id) {
        skuService.activateSKU(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("SKU activated successfully")
                .build());
    }

    @PutMapping("/{id}/price")
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    @Operation(summary = "Update SKU price")
    public ResponseEntity<ResponseData<Void>> updateSKUPrice(
            @PathVariable Long id,
            @RequestParam BigDecimal newPrice) {
        skuService.updateSKUPrice(id, newPrice);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("SKU price updated successfully")
                .build());
    }
}
