package com.example.Backend.dtos.invoice;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceRequest {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Invoice type is required")
    private String type; // vat_invoice, retail_receipt, credit_note

    private LocalDate dueDate;

    private BigDecimal taxRate = BigDecimal.ZERO;

    private String notes;
}
