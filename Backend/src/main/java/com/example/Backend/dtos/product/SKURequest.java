package com.example.Backend.dtos.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SKURequest {

    @NotNull(message = "Model ID is required")
    private Long modelId;

    private String variantName;

    private String color;

    private Integer storageGb;

    private String code;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be non-negative")
    private BigDecimal price;

    @DecimalMin(value = "0.0", inclusive = true, message = "Cost price must be non-negative")
    private BigDecimal costPrice;

    private Boolean isSerialized = true;

    private Boolean isActive = true;
}
