package com.example.Backend.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Entity
@Table(name = "payment")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private SalesOrder order;

    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.COMPLETED;

    @Column(name = "paid_at", nullable = false)
    @Builder.Default
    private LocalDateTime paidAt = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Getter
    public enum PaymentMethod {
        CASH("cash"),
        CARD("card"),
        TRANSFER("transfer"),
        OTHER("other");

        private final String value;

        PaymentMethod(String value) {
            this.value = value;
        }

    }

    @Getter
    public enum PaymentStatus {
        PENDING("pending"),
        COMPLETED("completed"),
        FAILED("failed"),
        CANCELLED("cancelled");

        private final String value;

        PaymentStatus(String value) {
            this.value = value;
        }

    }

    // Business logic methods
    public boolean isCompleted() {
        return status == PaymentStatus.COMPLETED;
    }

    public boolean canCancel() {
        return status == PaymentStatus.PENDING;
    }
}
