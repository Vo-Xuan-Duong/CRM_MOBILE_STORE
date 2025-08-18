package com.example.Backend.dtos.campaign;

import com.example.Backend.models.Campaign;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignRequest {

    @NotBlank(message = "Campaign name is required")
    private String name;

    @NotNull(message = "Campaign type is required")
    private Campaign.CampaignType type;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal budget;
}
