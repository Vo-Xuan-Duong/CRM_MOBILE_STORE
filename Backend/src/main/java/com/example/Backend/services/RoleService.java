package com.example.Backend.services;

import com.example.Backend.dtos.role.AddPermisstionRequest;
import com.example.Backend.dtos.role.RoleRequest;
import com.example.Backend.dtos.role.RoleResponse;
import com.example.Backend.exceptions.RoleException;
import com.example.Backend.mappers.RoleMapper;
import com.example.Backend.models.Role;
import com.example.Backend.models.Permission;
import com.example.Backend.repositorys.PermissionRepository;
import com.example.Backend.repositorys.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper,
                       PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
        this.permissionRepository = permissionRepository;
    }

    public RoleResponse createRole(RoleRequest roleRequest) {
        // Kiểm tra role code đã tồn tại
        if (roleRepository.existsByRole(roleRequest.getCode())) {
            throw new RoleException("Role code already exists: " + roleRequest.getCode());
        }

        Role role = roleMapper.toEntity(roleRequest);

        // Gán permissions nếu có
        if (roleRequest.getPermissionIds() != null && !roleRequest.getPermissionIds().isEmpty()) {
            Set<Permission> permissions = new HashSet<>();
            for (Long permissionId : roleRequest.getPermissionIds()) {
                Permission permission = permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new RoleException("Permission not found: " + permissionId));
                permissions.add(permission);
            }
            role.setPermissions(permissions);
        }

        Role savedRole = roleRepository.save(role);
        return roleMapper.mapToResponse(savedRole);
    }

    public RoleResponse updateRole(Long roleId, RoleRequest roleRequest) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleException("Role not found: " + roleId));

        // Kiểm tra role code conflict (nếu thay đổi role code)
        if (roleRequest.getCode() != null && !roleRequest.getCode().equals(role.getCode())) {
            if (roleRepository.existsByRole(roleRequest.getCode())) {
                throw new RoleException("Role code already exists: " + roleRequest.getCode());
            }
        }

        roleMapper.updateEntityFromRequest(role, roleRequest);

        // Cập nhật permissions nếu có
        if (roleRequest.getPermissionIds() != null) {
            Set<Permission> permissions = new HashSet<>();
            for (Long permissionId : roleRequest.getPermissionIds()) {
                Permission permission = permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new RoleException("Permission not found: " + permissionId));
                permissions.add(permission);
            }
            role.setPermissions(permissions);
        }

        Role updatedRole = roleRepository.save(role);
        return roleMapper.mapToResponse(updatedRole);
    }

    public void deleteRole(Long roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new RoleException("Role not found: " + roleId);
        }
        roleRepository.deleteById(roleId);
    }

    public RoleResponse getRoleById(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleException("Role not found: " + roleId));
        return roleMapper.mapToResponse(role);
    }

    public RoleResponse getRoleByCode(String roleCode) {
        Role role = roleRepository.findByRole(roleCode)
                .orElseThrow(() -> new RoleException("Role not found: " + roleCode));
        return roleMapper.mapToResponse(role);
    }

    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(roleMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    public Page<RoleResponse> getRolesPaginated(Pageable pageable) {
        return roleRepository.findAll(pageable)
                .map(roleMapper::mapToResponse);
    }

    public void addPermissionsToRole(Long roleId, AddPermisstionRequest addPermissionRequest) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleException("Role not found: " + roleId));

        addPermissionRequest.getPermissions().forEach(permissionCode -> {
            Permission permission = permissionRepository.findByCode(permissionCode)
                    .orElseThrow(() -> new RoleException("Permission not found: " + permissionCode));
            role.getPermissions().add(permission);
        });

        roleRepository.save(role);
    }

    public void removePermissionFromRole(Long roleId, String permissionCode) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleException("Role not found: " + roleId));

        Permission permission = permissionRepository.findByCode(permissionCode)
                .orElseThrow(() -> new RoleException("Permission not found: " + permissionCode));

        role.getPermissions().remove(permission);
        roleRepository.save(role);
    }

    public List<RoleResponse> getRolesByPermission(String permissionCode) {
        return roleRepository.findByPermissions_Code(permissionCode).stream()
                .map(roleMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    public boolean existsByCode(String roleCode) {
        return roleRepository.existsByRole(roleCode);
    }

    public long countRoles() {
        return roleRepository.count();
    }
}
