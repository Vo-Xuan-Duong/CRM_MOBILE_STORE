package com.example.Backend.dtos.permission;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionRequest {

    @NotBlank(message = "Permission code is required")
    private String code;

    @NotBlank(message = "Permission name is required")
    private String name;

    private String description;
}
