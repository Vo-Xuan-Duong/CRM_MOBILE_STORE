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
public class MarketingCampaignDto {
    private Long id;
    private String name;
    private String description;
    private String type;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String content;
    private String discountCode;
    private Integer discountPercentage;
    private Integer targetAgeMin;
    private Integer targetAgeMax;
    private String targetGender;
    private String targetTier;
    private Integer sentCount;
    private Integer openCount;
    private Integer clickCount;
    private Double openRate;
    private Double clickRate;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
