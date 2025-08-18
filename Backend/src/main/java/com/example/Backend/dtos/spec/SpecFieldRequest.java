package com.example.Backend.dtos.spec;

import com.example.Backend.models.SpecField;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecFieldRequest {

    @NotNull(message = "Group ID is required")
    private Long groupId;

    @NotBlank(message = "Field key is required")
    private String fieldKey;

    @NotBlank(message = "Label is required")
    private String label;

    @NotNull(message = "Data type is required")
    private SpecField.DataType dataType;

    private String unit;

    private SpecField.AppliesTo appliesTo = SpecField.AppliesTo.MODEL;

    private Integer sortOrder = 0;

    private Boolean isRequired = false;
}
