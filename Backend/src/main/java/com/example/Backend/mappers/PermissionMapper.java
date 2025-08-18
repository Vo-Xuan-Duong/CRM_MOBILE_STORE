package com.example.Backend.mappers;

import com.example.Backend.dtos.permission.PermissionRequest;
import com.example.Backend.dtos.permission.PermissionResponse;
import com.example.Backend.models.Permission;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper {

    public PermissionResponse toResponse(Permission permission) {
        if (permission == null) return null;

        return PermissionResponse.builder()
                .id(permission.getId())
                .code(permission.getCode())
                .name(permission.getName())
                .description(permission.getDescription())
                .createdAt(permission.getCreatedAt())
                .updatedAt(permission.getUpdatedAt())
                .build();
    }

    public Permission toEntity(PermissionRequest request) {
        if (request == null) return null;

        return Permission.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public void updateEntityFromRequest(Permission permission, PermissionRequest request) {
        if (permission == null || request == null) return;

        if (request.getCode() != null) {
            permission.setCode(request.getCode());
        }
        if (request.getName() != null) {
            permission.setName(request.getName());
        }
        if (request.getDescription() != null) {
            permission.setDescription(request.getDescription());
        }
    }
}
