package com.example.Backend.dtos.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReportDto {
    private Long productId;
    private String productName;
    private String sku;
    private String brand;
    private String category;
    private Integer currentStock;
    private Integer soldQuantity;
    private BigDecimal revenue;
    private BigDecimal profitMargin;
    private Integer reorderLevel;
    private String stockStatus; // IN_STOCK, LOW_STOCK, OUT_OF_STOCK
}
