package com.example.Backend.mappers;

import com.example.Backend.dtos.role.RoleRequest;
import com.example.Backend.dtos.role.RoleResponse;
import com.example.Backend.models.Role;
import com.example.Backend.models.Permission;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RoleMapper {

    public RoleResponse mapToResponse(Role role) {
        if (role == null) return null;

        Set<RoleResponse.PermissionInfo> permissionInfos = role.getPermissions() != null ?
            role.getPermissions().stream()
                .map(this::mapPermissionToInfo)
                .collect(Collectors.toSet()) : Set.of();

        return RoleResponse.builder()
                .id(role.getId())
                .code(role.getCode())
                .name(role.getName())
                .description(role.getDescription())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .permissions(permissionInfos)
                .build();
    }

    public Role toEntity(RoleRequest request) {
        if (request == null) return null;

        return Role.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public void updateEntityFromRequest(Role role, RoleRequest request) {
        if (role == null || request == null) return;

        if (request.getCode() != null) {
            role.setCode(request.getCode());
        }
        if (request.getName() != null) {
            role.setName(request.getName());
        }
        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }
    }

    private RoleResponse.PermissionInfo mapPermissionToInfo(Permission permission) {
        return RoleResponse.PermissionInfo.builder()
                .id(permission.getId())
                .code(permission.getCode())
                .name(permission.getName())
                .description(permission.getDescription())
                .build();
    }
}
