package com.example.Backend.dtos.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockItemResponse {

    private Long id;
    private Long skuId;
    private String skuName;
    private String brandName;
    private Integer quantity;
    private Integer reservedQty;
    private Integer availableQty;
    private Integer minStock;
    private Integer maxStock;
    private Boolean isLowStock;
    private LocalDateTime updatedAt;
}
