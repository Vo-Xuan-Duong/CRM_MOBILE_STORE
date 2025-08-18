package com.example.Backend.dtos.customercare;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerFeedbackDto {
    private Long id;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private Long orderId;
    private String orderNumber;
    private String subject;
    private String content;
    private String type;
    private Integer rating;
    private String status;
    private String response;
    private LocalDateTime respondedAt;
    private String respondedByName;
    private Boolean isPositive;
    private Boolean isNegative;
    private Boolean requiresUrgentAttention;
    private Long daysSinceCreated;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
