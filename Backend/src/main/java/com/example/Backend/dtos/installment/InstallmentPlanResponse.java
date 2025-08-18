package com.example.Backend.dtos.installment;

import com.example.Backend.models.InstallmentPlan;
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
public class InstallmentPlanResponse {

    private Long id;
    private Long orderId;
    private String orderNumber;
    private String customerName;
    private String provider;
    private BigDecimal principal;
    private BigDecimal downPayment;
    private BigDecimal totalAmount;
    private Integer months;
    private BigDecimal interestRateApr;
    private BigDecimal monthlyPayment;
    private BigDecimal remainingBalance;
    private InstallmentPlan.InstallmentStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate nextPaymentDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
