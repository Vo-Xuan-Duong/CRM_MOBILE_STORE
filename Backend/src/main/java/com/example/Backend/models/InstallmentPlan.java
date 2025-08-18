package com.example.Backend.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "installment_plan")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallmentPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private SalesOrder order;

    @NotNull
    @Column(nullable = false)
    private String provider;

    @NotNull
    @DecimalMin(value = "0.01", message = "Principal must be positive")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal principal;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "down_payment", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal downPayment = BigDecimal.ZERO;

    @Min(value = 1, message = "Months must be positive")
    @Column(nullable = false)
    private Integer months;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "interest_rate_apr", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal interestRateApr = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.01", message = "Monthly payment must be positive")
    @Column(name = "monthly_payment", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyPayment;

    @Column(name = "next_due_date")
    private LocalDateTime nextDueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private InstallmentStatus status = InstallmentStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum InstallmentStatus {
        ACTIVE("active"),
        COMPLETED("completed"),
        DEFAULTED("defaulted"),
        CANCELLED("cancelled");

        private final String value;

        InstallmentStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    // Business logic methods
    public boolean isActive() {
        return status == InstallmentStatus.ACTIVE;
    }

    public boolean isCompleted() {
        return status == InstallmentStatus.COMPLETED;
    }

    public BigDecimal getTotalAmount() {
        return monthlyPayment.multiply(BigDecimal.valueOf(months));
    }

    public BigDecimal getTotalInterest() {
        return getTotalAmount().subtract(principal);
    }
}
