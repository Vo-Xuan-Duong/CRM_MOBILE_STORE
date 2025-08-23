package com.example.Backend.dtos.spec;

import com.example.Backend.models.SpecField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Long groupId;
    private String groupName;

    // Target references
    private Long productModelId;
    private String productModelName;
    private Long skuId;
    private String skuCode;

    // Value (will be cast based on dataType)
    private Object value;
    private String displayValue; // Formatted value for display

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
