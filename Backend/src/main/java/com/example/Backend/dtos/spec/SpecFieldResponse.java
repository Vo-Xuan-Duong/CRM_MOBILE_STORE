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
public class SpecFieldResponse {

    private Long id;
    private Long groupId;
    private String groupName;
    private String fieldKey;
    private String label;
    private SpecField.DataType dataType;
    private String unit;
    private SpecField.AppliesTo appliesTo;
    private Integer sortOrder;
    private Boolean isRequired;
    private LocalDateTime createdAt;
}
