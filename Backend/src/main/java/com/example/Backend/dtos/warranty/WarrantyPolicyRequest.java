package com.example.Backend.dtos.warranty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarrantyPolicyRequest {

    private UUID brandId;
    private UUID modelId;
    private UUID variantId;

    @NotNull(message = "Warranty months is required")
    @Positive(message = "Warranty months must be positive")
    private Integer months;

    private String conditions;
}
