package com.example.Backend.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "invoice")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private SalesOrder order;

    @NotBlank(message = "Invoice number is required")
    @Column(name = "invoice_no", nullable = false, unique = true)
    private String invoiceNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    @Column(name = "issued_at", nullable = false)
    @Builder.Default
    private LocalDateTime issuedAt = LocalDateTime.now();

    @Column(name = "due_date")
    private LocalDate dueDate;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "tax_rate", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal taxRate = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "tax_amount", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "total_with_tax", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalWithTax = BigDecimal.ZERO;

    @Column(name = "pdf_url")
    private String pdfUrl;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum InvoiceType {
        VAT_INVOICE("vat_invoice"),
        RETAIL_RECEIPT("retail_receipt"),
        CREDIT_NOTE("credit_note");

        private final String value;

        InvoiceType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum InvoiceStatus {
        DRAFT("draft"),
        ISSUED("issued"),
        SENT("sent"),
        PAID("paid"),
        VOID("void"),
        CANCELLED("cancelled");

        private final String value;

        InvoiceStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    // Business logic methods
    public boolean isIssued() {
        return status != InvoiceStatus.DRAFT;
    }

    public boolean isPaid() {
        return status == InvoiceStatus.PAID;
    }

    public boolean canVoid() {
        return status == InvoiceStatus.ISSUED || status == InvoiceStatus.SENT;
    }

    public boolean isOverdue() {
        return dueDate != null && LocalDate.now().isAfter(dueDate) && !isPaid();
    }
}
