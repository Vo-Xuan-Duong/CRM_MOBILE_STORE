package com.example.Backend.dtos.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderResponse {

    private Long id;
    private String orderNumber;
    private Long customerId;
    private String customerName;
    private Long userId;
    private String userName;
    private OrderStatus status;
    private String paymentMethod;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal taxAmount;
    private BigDecimal total;
    private String notes;
    private LocalDate orderDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SalesOrderItemResponse> items;

    public enum OrderStatus {
        DRAFT, PENDING, CONFIRMED, PAID, CANCELLED, REFUNDED
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalesOrderItemResponse {
        private Long id;
        private Long skuId;
        private String skuName;
        private Long serialUnitId;
        private String serialNumber;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal discount;
        private BigDecimal lineTotal;
        private Integer warrantyMonths;
    }
}
