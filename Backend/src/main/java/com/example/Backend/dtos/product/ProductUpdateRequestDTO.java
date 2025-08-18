package com.example.Backend.dtos.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequestDTO {

    private UUID id;
    private String sku;
    private String name;
    private UUID brandId;
    private UUID categoryId;
    private UUID modelId;
    private UUID variantId;
    private String gtin;
    private BigDecimal unitPrice;
    private Map<String, Object> attributes;
    private Boolean isActive;
}
