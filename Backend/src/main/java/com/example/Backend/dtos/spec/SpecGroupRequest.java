package com.example.Backend.dtos.spec;

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

    @NotBlank(message = "Spec group name is required")
    private String name;

    private Integer sortOrder = 0;

    private Boolean isActive = true;
}
