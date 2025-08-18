package com.example.Backend.dtos.role;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddPermisstionRequest {

    @NotNull(message = "Role ID is required")
    private Long roleId;

    @NotNull(message = "At least one permission code is required")
    private Set<String> permissions;
}
