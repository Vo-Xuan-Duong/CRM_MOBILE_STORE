package com.example.Backend.dtos.campaign;

import com.example.Backend.models.Campaign;
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
public class CampaignResponse {

    private Long id;
    private String name;
    private Campaign.CampaignType type;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal budget;
    private Campaign.CampaignStatus status;
    private Long createdById;
    private String createdByName;
    private Boolean isActive;
    private Boolean isRunning;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
