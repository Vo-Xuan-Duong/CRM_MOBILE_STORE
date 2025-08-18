package com.example.Backend.dtos.product;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchRequest {

    private String name;

    @Size(max = 50, message = "Mã SKU không được quá 50 ký tự")
    private String sku;

    private String gtin;
    private UUID brandId;
    private UUID categoryId;
    private UUID modelId;
    private UUID variantId;

    // Lọc theo giá
    @DecimalMin(value = "0.0", message = "Giá tối thiểu phải >= 0")
    private BigDecimal minPrice;

    @DecimalMin(value = "0.0", message = "Giá tối đa phải >= 0")
    private BigDecimal maxPrice;

    private Boolean isActive;
    private LocalDateTime createdFrom;
    private LocalDateTime createdTo;

    // Search by specifications
    private String os;
    private String chipset;
    private Integer minRam;
    private Integer maxRam;
    private Integer minStorage;
    private Integer maxStorage;
    private String color;

    // Pagination
    @Builder.Default
    private Integer page = 0;
    @Builder.Default
    private Integer size = 20;
    @Builder.Default
    private String sortBy = "createdAt";
    @Builder.Default
    private String sortDirection = "DESC";
}
