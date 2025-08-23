package com.example.Backend.dtos.spec;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecGroupRequest {

    @NotBlank(message = "Tên nhóm spec không được để trống")
    private String name;

    @Min(value = 0, message = "Thứ tự sắp xếp phải >= 0")
    @Builder.Default
    private Integer sortOrder = 0;

    private Boolean isActive;
}
