package com.example.Backend.dtos.payment;

import com.example.Backend.models.Payment;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateDTO {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Payment method is required")
    private Payment.PaymentMethod method; // cash, card, bank, momo, vnpay, other

    @Builder.Default
    private String status = "completed"; // pending, completed, failed, refunded, cancelled

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @Builder.Default
    private LocalDateTime paidAt = LocalDateTime.now();
}
