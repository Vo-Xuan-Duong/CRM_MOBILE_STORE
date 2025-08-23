package com.example.Backend.dtos.salesorder;

import com.example.Backend.models.Payment;
import com.example.Backend.models.SalesOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderResponse {

    private Long id;
    private Long customerId;
    private String customerName;
    private Long userId;
    private String userName;
    private SalesOrder.OrderStatus status;
    private Payment.PaymentMethod paymentMethod;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal discount;
    private BigDecimal total;
    private String notes;
    private List<SalesOrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
