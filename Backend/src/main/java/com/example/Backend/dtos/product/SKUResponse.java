package com.example.Backend.dtos.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SKUResponse {

    private Long id;
    private Long modelId;
    private String modelName;
    private String brandName;
    private String variantName;
    private String color;
    private Integer storageGb;
    private String code;
    private BigDecimal price;
    private BigDecimal costPrice;
    private Boolean isSerialized;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
