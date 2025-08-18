package com.example.Backend.dtos.report;

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
public class CustomerReportDto {
    private Long customerId;
    private String customerName;
    private String phoneNumber;
    private String email;
    private Long totalOrders;
    private BigDecimal totalSpent;
    private LocalDateTime lastOrderDate;
    private LocalDateTime registrationDate;
    private String customerStatus;
    private String customerSegment; // VIP, REGULAR, NEW
}
