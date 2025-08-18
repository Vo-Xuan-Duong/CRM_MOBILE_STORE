package com.example.Backend.dtos.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSummaryDTO {

    private UUID id;
    private String sku;
    private String name;
    private BigDecimal unitPrice;
    private Boolean isActive;

    // Basic info from related entities
    private String brandName;
    private String categoryName;
    private String modelName;
    private String variantColor;
    private Integer variantRamGb;
    private Integer variantStorageGb;
}
