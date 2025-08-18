package com.example.Backend.dtos.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDTO {

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotBlank(message = "Product name is required")
    private String name;

    private Long brandId;
    private Long categoryId;
    private Long modelId;
    private Long variantId;

    private String gtin;

    @NotNull(message = "Unit price is required")
    @PositiveOrZero(message = "Unit price must be positive or zero")
    private BigDecimal unitPrice;

    private Map<String, Object> attributes;

    @Builder.Default
    private Boolean isActive = true;
}
