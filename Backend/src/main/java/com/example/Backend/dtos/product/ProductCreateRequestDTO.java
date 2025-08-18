package com.example.Backend.dtos.product;

import jakarta.validation.constraints.NotBlank;
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
public class ProductCreateRequestDTO {

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotBlank(message = "Product name is required")
    private String name;

    private UUID brandId;
    private UUID categoryId;
    private UUID modelId;
    private UUID variantId;

    private String gtin;
    private BigDecimal unitPrice;
    private Map<String, Object> attributes;

    @Builder.Default
    private Boolean isActive = true;
}
