package com.example.Backend.dtos.warranty;

import com.example.Backend.models.Warranty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarrantyResponse {

    private Long id;
    private Long customerId;
    private String customerName;
    private Long orderItemId;
    private Long serialUnitId;
    private String imei;
    private String warrantyCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer months;
    private Warranty.WarrantyStatus status;
    private String qrImageUrl;
    private String notes;
    private Boolean isActive;
    private Boolean isExpired;
    private Long daysRemaining;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
