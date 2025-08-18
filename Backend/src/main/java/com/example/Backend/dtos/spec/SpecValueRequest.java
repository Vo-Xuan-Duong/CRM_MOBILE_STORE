package com.example.Backend.dtos.spec;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecValueRequest {

    @NotNull(message = "Field ID is required")
    private Long fieldId;

    private Long modelId;

    private Long skuId;

    private String valueText;

    private BigDecimal valueNumber;

    private Boolean valueBool;

    private String valueJson;

    private String unitOverride;
}
