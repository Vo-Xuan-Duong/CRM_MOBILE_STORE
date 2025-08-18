package com.example.Backend.dtos.order;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    private String paymentMethod;

    private BigDecimal discount = BigDecimal.ZERO;

    private String notes;

    private List<SalesOrderItemRequest> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalesOrderItemRequest {
        @NotNull(message = "SKU ID is required")
        private Long skuId;

        private Long serialUnitId;

        @NotNull(message = "Quantity is required")
        private Integer quantity;

        private BigDecimal unitPrice;

        private BigDecimal discount = BigDecimal.ZERO;

        private Integer warrantyMonths = 12;
    }
}
