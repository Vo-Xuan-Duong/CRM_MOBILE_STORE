package com.example.Backend.dtos.spec;

import com.example.Backend.models.SpecField;
import jakarta.validation.constraints.Min;
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

    @NotNull(message = "Group ID không được để trống")
    private Long groupId;

    @NotBlank(message = "Field key không được để trống")
    private String fieldKey;

    @NotBlank(message = "Label không được để trống")
    private String label;

    @NotNull(message = "Data type không được để trống")
    private SpecField.DataType dataType;

    private String unit;

    @Builder.Default
    private SpecField.AppliesTo appliesTo = SpecField.AppliesTo.MODEL;

    @Min(value = 0, message = "Thứ tự sắp xếp phải >= 0")
    @Builder.Default
    private Integer sortOrder = 0;

    @Builder.Default
    private Boolean isRequired = false;

    private Boolean isActive;
}
