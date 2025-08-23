package com.example.Backend.services;

import com.example.Backend.dtos.role.AddPermisstionRequest;
import com.example.Backend.dtos.role.RoleRequest;
import com.example.Backend.dtos.role.RoleResponse;
import com.example.Backend.exceptions.RoleException;
import com.example.Backend.mappers.RoleMapper;
import com.example.Backend.models.Permission;
import com.example.Backend.models.Role;
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
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    public RoleService(RoleRepository roleRepository,
                       PermissionRepository permissionRepository,
                       RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.roleMapper = roleMapper;
    }

    public RoleResponse createRole(RoleRequest request) {
        // Kiểm tra role code đã tồn tại
        if (roleRepository.existsByCode(request.getCode())) {
            throw new RoleException("Role code already exists: " + request.getCode());
        }

        Role role = roleMapper.toEntity(request);

        // Gán permissions nếu có
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            Set<Permission> permissions = new HashSet<>();
            for (Long permissionId : request.getPermissionIds()) {
                Permission permission = permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new RoleException("Permission not found: " + permissionId));
                permissions.add(permission);
            }
            role.setPermissions(permissions);
        }

        Role savedRole = roleRepository.save(role);
        return roleMapper.mapToResponse(savedRole);
    }

    public RoleResponse updateRole(Long id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleException("Role not found: " + id));

        // Kiểm tra role code conflict (nếu thay đổi code)
        if (request.getCode() != null && !request.getCode().equals(role.getCode())) {
            if (roleRepository.existsByCode(request.getCode())) {
                throw new RoleException("Role code already exists: " + request.getCode());
            }
        }

        roleMapper.updateEntityFromRequest(role, request);

        // Cập nhật permissions nếu có
        if (request.getPermissionIds() != null) {
            Set<Permission> permissions = new HashSet<>();
            for (Long permissionId : request.getPermissionIds()) {
                Permission permission = permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new RoleException("Permission not found: " + permissionId));
                permissions.add(permission);
            }
            role.setPermissions(permissions);
        }

        Role updatedRole = roleRepository.save(role);
        return roleMapper.mapToResponse(updatedRole);
    }

    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new RoleException("Role not found: " + id);
        }
        roleRepository.deleteById(id);
    }

    public RoleResponse getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleException("Role not found: " + id));
        return roleMapper.mapToResponse(role);
    }

    public RoleResponse getRoleByCode(String code) {
        Role role = roleRepository.findByCode(code)
                .orElseThrow(() -> new RoleException("Role not found: " + code));
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

    public List<RoleResponse> searchRolesByName(String name) {
        return roleRepository.findByNameContainingIgnoreCase(name).stream()
                .map(roleMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<RoleResponse> getRolesByPermissionCode(String permissionCode) {
        return roleRepository.findByPermissions_Code(permissionCode).stream()
                .map(roleMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<RoleResponse> getActiveRoles() {
        return roleRepository.findByIsActive(true).stream()
                .map(roleMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<RoleResponse> getInactiveRoles() {
        return roleRepository.findByIsActive(false).stream()
                .map(roleMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    public RoleResponse addPermissionsToRole(Long roleId, AddPermisstionRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleException("Role not found: " + roleId));

        Set<Permission> currentPermissions = role.getPermissions();
        if (currentPermissions == null) {
            currentPermissions = new HashSet<>();
        }

        for (String permissionCode : request.getPermissions()) {
            Permission permission = permissionRepository.findByCode(permissionCode)
                    .orElseThrow(() -> new RoleException("Permission not found: " + permissionCode));
            currentPermissions.add(permission);
        }

        role.setPermissions(currentPermissions);
        Role updatedRole = roleRepository.save(role);
        return roleMapper.mapToResponse(updatedRole);
    }

    public RoleResponse removePermissionsFromRole(Long roleId, AddPermisstionRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleException("Role not found: " + roleId));

        Set<Permission> currentPermissions = role.getPermissions();
        if (currentPermissions != null) {
            for (String permissionCode : request.getPermissions()) {
                currentPermissions.removeIf(p -> p.getCode().equals(permissionCode));
            }
            role.setPermissions(currentPermissions);
        }

        Role updatedRole = roleRepository.save(role);
        return roleMapper.mapToResponse(updatedRole);
    }

    public RoleResponse toggleRoleStatus(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleException("Role not found: " + id));

        role.setIsActive(!role.getIsActive());
        Role updatedRole = roleRepository.save(role);
        return roleMapper.mapToResponse(updatedRole);
    }

    public boolean existsByCode(String code) {
        return roleRepository.existsByCode(code);
    }

    public long countRoles() {
        return roleRepository.count();
    }

    public long countActiveRoles() {
        return roleRepository.findByIsActive(true).size();
    }

    public long countInactiveRoles() {
        return roleRepository.findByIsActive(false).size();
    }

    public List<RoleResponse> getRolesByPermission(String permissionCode) {
        List<Role> roles = roleRepository.findByPermissions_Code(permissionCode);
        return roles.stream()
                .map(roleMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    public void removePermissionFromRole(Long id, String permissionCode) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleException("Role not found: " + id));

        Set<Permission> permissions = role.getPermissions();
        if (permissions == null || permissions.isEmpty()) {
            throw new RoleException("No permissions assigned to this role");
        }

        Permission permissionToRemove = permissions.stream()
                .filter(p -> p.getCode().equals(permissionCode))
                .findFirst()
                .orElseThrow(() -> new RoleException("Permission not found: " + permissionCode));

        permissions.remove(permissionToRemove);
        role.setPermissions(permissions);
        roleRepository.save(role);
    }
}
