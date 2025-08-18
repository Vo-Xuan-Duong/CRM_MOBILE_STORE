package com.example.Backend.services;

import com.example.Backend.dtos.permission.PermissionRequest;
import com.example.Backend.dtos.permission.PermissionResponse;
import com.example.Backend.exceptions.PermissionException;
import com.example.Backend.mappers.PermissionMapper;
import com.example.Backend.models.Permission;
import com.example.Backend.repositorys.PermissionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    public PermissionService(PermissionRepository permissionRepository, PermissionMapper permissionMapper) {
        this.permissionRepository = permissionRepository;
        this.permissionMapper = permissionMapper;
    }

    public PermissionResponse createPermission(PermissionRequest request) {
        // Kiểm tra permission code đã tồn tại
        if (permissionRepository.existsByCode(request.getCode())) {
            throw new PermissionException("Permission code already exists: " + request.getCode());
        }

        Permission permission = permissionMapper.toEntity(request);
        Permission savedPermission = permissionRepository.save(permission);
        return permissionMapper.toResponse(savedPermission);
    }

    public PermissionResponse updatePermission(Long id, PermissionRequest request) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new PermissionException("Permission not found: " + id));

        // Kiểm tra permission code conflict (nếu thay đổi code)
        if (request.getCode() != null && !request.getCode().equals(permission.getCode())) {
            if (permissionRepository.existsByCode(request.getCode())) {
                throw new PermissionException("Permission code already exists: " + request.getCode());
            }
        }

        permissionMapper.updateEntityFromRequest(permission, request);
        Permission updatedPermission = permissionRepository.save(permission);
        return permissionMapper.toResponse(updatedPermission);
    }

    public void deletePermission(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new PermissionException("Permission not found: " + id);
        }
        permissionRepository.deleteById(id);
    }

    public PermissionResponse getPermissionById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new PermissionException("Permission not found: " + id));
        return permissionMapper.toResponse(permission);
    }

    public PermissionResponse getPermissionByCode(String code) {
        Permission permission = permissionRepository.findByCode(code)
                .orElseThrow(() -> new PermissionException("Permission not found: " + code));
        return permissionMapper.toResponse(permission);
    }

    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(permissionMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Page<PermissionResponse> getPermissionsPaginated(Pageable pageable) {
        return permissionRepository.findAll(pageable)
                .map(permissionMapper::toResponse);
    }

    public List<PermissionResponse> searchPermissionsByName(String name) {
        return permissionRepository.findByNameContainingIgnoreCase(name).stream()
                .map(permissionMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<PermissionResponse> searchPermissionsByCode(String code) {
        return permissionRepository.findByCodeContainingIgnoreCase(code).stream()
                .map(permissionMapper::toResponse)
                .collect(Collectors.toList());
    }

    public boolean existsByCode(String code) {
        return permissionRepository.existsByCode(code);
    }

    public long countPermissions() {
        return permissionRepository.count();
    }
}
