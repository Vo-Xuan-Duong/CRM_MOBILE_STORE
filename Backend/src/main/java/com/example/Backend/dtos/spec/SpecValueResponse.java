package com.example.Backend.dtos.spec;

import com.example.Backend.models.SpecField;
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
public class SpecValueResponse {

    private Long id;
    private Long fieldId;
    private String fieldKey;
    private String fieldLabel;
    private SpecField.DataType dataType;
    private String unit;
    private Long modelId;
    private String modelName;
    private Long skuId;
    private String skuName;
    private String valueText;
    private BigDecimal valueNumber;
    private Boolean valueBool;
    private String valueJson;
    private String unitOverride;
    private String displayValue;
    private LocalDateTime createdAt;
}
