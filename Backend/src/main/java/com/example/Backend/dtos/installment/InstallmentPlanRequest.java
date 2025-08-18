package com.example.Backend.dtos.installment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallmentPlanRequest {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotBlank(message = "Provider is required")
    private String provider;

    @NotNull(message = "Principal amount is required")
    @DecimalMin(value = "0.01", message = "Principal must be positive")
    private BigDecimal principal;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal downPayment = BigDecimal.ZERO;

    @NotNull(message = "Number of months is required")
    @Min(value = 1, message = "Months must be positive")
    private Integer months;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal interestRateApr = BigDecimal.ZERO;

    @NotNull(message = "Monthly payment is required")
    @DecimalMin(value = "0.01", message = "Monthly payment must be positive")
    private BigDecimal monthlyPayment;
}
