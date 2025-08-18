package com.example.Backend.dtos.customercare;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerLoyaltyDto {
    private Long id;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private Integer totalPoints;
    private Integer usedPoints;
    private Integer availablePoints;
    private BigDecimal totalSpent;
    private String tier;
    private LocalDateTime lastPurchaseDate;
    private LocalDateTime tierUpgradeDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
