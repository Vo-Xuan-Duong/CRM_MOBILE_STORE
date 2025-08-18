package com.example.Backend.dtos.campaign;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignTargetRequest {

    @NotEmpty(message = "Customer IDs list cannot be empty")
    private List<Long> customerIds;
}
