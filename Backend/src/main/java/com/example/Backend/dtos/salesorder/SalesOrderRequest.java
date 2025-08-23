package com.example.Backend.dtos.salesorder;

import com.example.Backend.models.Payment;
import com.example.Backend.models.SalesOrder;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SalesOrderRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;
    private String notes;
    private BigDecimal discount;
    private Payment.PaymentMethod paymentMethod;

    @NotNull(message = "Items are required")
    private List<SalesOrderItemRequest> items;
}
