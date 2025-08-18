package com.example.Backend.dtos.invoice;

import com.example.Backend.models.Invoice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {

    private Long id;
    private Long orderId;
    private String orderNumber;
    private String invoiceNo;
    private Invoice.InvoiceType type;
    private Invoice.InvoiceStatus status;
    private LocalDateTime issuedAt;
    private LocalDate dueDate;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal subtotal;
    private BigDecimal totalWithTax;
    private String pdfUrl;
    private String notes;
    private Long createdById;
    private String createdByName;
    private Boolean isOverdue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
