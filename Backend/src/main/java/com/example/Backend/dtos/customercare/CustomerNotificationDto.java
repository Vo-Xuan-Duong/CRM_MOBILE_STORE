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
public class CustomerNotificationDto {
    private Long id;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private String title;
    private String content;
    private String type;
    private String channel;
    private String status;
    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    private String errorMessage;
    private Long campaignId;
    private String campaignName;
    private Boolean isRead;
    private Boolean isScheduled;
    private LocalDateTime createdAt;
}
