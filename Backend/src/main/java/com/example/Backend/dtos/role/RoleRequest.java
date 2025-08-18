package com.example.Backend.dtos.role;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequest {

    @NotBlank(message = "Role code is required")
    private String code;
    @NotBlank(message = "Role name is required")
    private String name;
    private String description;
    private Set<Long> permissionIds;
}
