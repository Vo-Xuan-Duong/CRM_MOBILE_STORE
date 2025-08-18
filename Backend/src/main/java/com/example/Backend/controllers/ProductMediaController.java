package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.dtos.media.ProductMediaRequest;
import com.example.Backend.dtos.media.ProductMediaResponse;
import com.example.Backend.services.ProductMediaService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product-media")
@RequiredArgsConstructor
@Tag(name = "Product Media", description = "Product Media Management API")
public class ProductMediaController {

    private final ProductMediaService productMediaService;

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT_CREATE')")
    @Operation(summary = "Upload product media")
    public ResponseEntity<ResponseData<ProductMediaResponse>> uploadProductMedia(
            @Valid @RequestBody ProductMediaRequest request) {
        ProductMediaResponse response = productMediaService.uploadProductMedia(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<ProductMediaResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Product media uploaded successfully")
                        .data(response)
                        .build());
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('PRODUCT_CREATE')")
    @Operation(summary = "Upload media file")
    public ResponseEntity<ResponseData<ProductMediaResponse>> uploadMediaFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Long modelId,
            @RequestParam(required = false) Long skuId,
            @RequestParam String mediaType,
            @RequestParam(required = false) String caption) {
        ProductMediaResponse response = productMediaService.uploadMediaFile(file, modelId, skuId, mediaType, caption);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<ProductMediaResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Media file uploaded successfully")
                        .data(response)
                        .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @Operation(summary = "Get product media by ID")
    public ResponseEntity<ResponseData<ProductMediaResponse>> getProductMediaById(@PathVariable Long id) {
        ProductMediaResponse response = productMediaService.getProductMediaById(id);
        return ResponseEntity.ok(ResponseData.<ProductMediaResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Product media retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/model/{modelId}")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @Operation(summary = "Get media by product model")
    public ResponseEntity<ResponseData<List<ProductMediaResponse>>> getMediaByProductModel(@PathVariable Long modelId) {
        List<ProductMediaResponse> response = productMediaService.getMediaByProductModel(modelId);
        return ResponseEntity.ok(ResponseData.<List<ProductMediaResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Media by product model retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/sku/{skuId}")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @Operation(summary = "Get media by SKU")
    public ResponseEntity<ResponseData<List<ProductMediaResponse>>> getMediaBySku(@PathVariable Long skuId) {
        List<ProductMediaResponse> response = productMediaService.getMediaBySku(skuId);
        return ResponseEntity.ok(ResponseData.<List<ProductMediaResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Media by SKU retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/type/{mediaType}")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @Operation(summary = "Get media by type")
    public ResponseEntity<ResponseData<List<ProductMediaResponse>>> getMediaByType(@PathVariable String mediaType) {
        List<ProductMediaResponse> response = productMediaService.getMediaByType(mediaType);
        return ResponseEntity.ok(ResponseData.<List<ProductMediaResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Media by type retrieved successfully")
                .data(response)
                .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    @Operation(summary = "Update product media")
    public ResponseEntity<ResponseData<ProductMediaResponse>> updateProductMedia(
            @PathVariable Long id,
            @Valid @RequestBody ProductMediaRequest request) {
        ProductMediaResponse response = productMediaService.updateProductMedia(id, request);
        return ResponseEntity.ok(ResponseData.<ProductMediaResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Product media updated successfully")
                .data(response)
                .build());
    }

    @PutMapping("/{id}/set-primary")
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    @Operation(summary = "Set media as primary")
    public ResponseEntity<ResponseData<Void>> setAsPrimary(@PathVariable Long id) {
        productMediaService.setAsPrimary(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Media set as primary successfully")
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_DELETE')")
    @Operation(summary = "Delete product media")
    public ResponseEntity<ResponseData<Void>> deleteProductMedia(@PathVariable Long id) {
        productMediaService.deleteProductMedia(id);
        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Product media deleted successfully")
                .build());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @Operation(summary = "Get all product media with pagination")
    public ResponseEntity<ResponseData<Page<ProductMediaResponse>>> getAllProductMedia(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ProductMediaResponse> response = productMediaService.getAllProductMedia(pageable);
        return ResponseEntity.ok(ResponseData.<Page<ProductMediaResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Product media retrieved successfully")
                .data(response)
                .build());
    }
}
