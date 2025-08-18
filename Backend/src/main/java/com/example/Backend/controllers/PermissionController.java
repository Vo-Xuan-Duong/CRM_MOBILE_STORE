package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.dtos.permission.PermissionRequest;
import com.example.Backend.dtos.permission.PermissionResponse;
import com.example.Backend.services.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@Tag(name = "Permission Management", description = "APIs for managing permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    @Operation(summary = "Get all permissions with pagination")
    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    public ResponseEntity<ResponseData<Page<PermissionResponse>>> getAllPermissions(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PermissionResponse> permissions = permissionService.getPermissionsPaginated(pageable);

        return ResponseEntity.ok(ResponseData.<Page<PermissionResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Permissions retrieved successfully")
                .data(permissions)
                .build());
    }

    @GetMapping("/all")
    @Operation(summary = "Get all permissions without pagination")
    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    public ResponseEntity<ResponseData<List<PermissionResponse>>> getAllPermissionsNoPagination() {
        List<PermissionResponse> permissions = permissionService.getAllPermissions();

        return ResponseEntity.ok(ResponseData.<List<PermissionResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("All permissions retrieved successfully")
                .data(permissions)
                .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get permission by ID")
    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    public ResponseEntity<ResponseData<PermissionResponse>> getPermissionById(
            @Parameter(description = "Permission ID") @PathVariable Long id) {

        PermissionResponse permission = permissionService.getPermissionById(id);

        return ResponseEntity.ok(ResponseData.<PermissionResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Permission retrieved successfully")
                .data(permission)
                .build());
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get permission by code")
    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    public ResponseEntity<ResponseData<PermissionResponse>> getPermissionByCode(
            @Parameter(description = "Permission code") @PathVariable String code) {

        PermissionResponse permission = permissionService.getPermissionByCode(code);

        return ResponseEntity.ok(ResponseData.<PermissionResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Permission retrieved successfully")
                .data(permission)
                .build());
    }

    @GetMapping("/search/name")
    @Operation(summary = "Search permissions by name")
    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    public ResponseEntity<ResponseData<List<PermissionResponse>>> searchPermissionsByName(
            @Parameter(description = "Search term") @RequestParam String name) {

        List<PermissionResponse> permissions = permissionService.searchPermissionsByName(name);

        return ResponseEntity.ok(ResponseData.<List<PermissionResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Permissions matching '" + name + "' retrieved successfully")
                .data(permissions)
                .build());
    }

    @GetMapping("/search/code")
    @Operation(summary = "Search permissions by code")
    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    public ResponseEntity<ResponseData<List<PermissionResponse>>> searchPermissionsByCode(
            @Parameter(description = "Search term") @RequestParam String code) {

        List<PermissionResponse> permissions = permissionService.searchPermissionsByCode(code);

        return ResponseEntity.ok(ResponseData.<List<PermissionResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Permissions matching '" + code + "' retrieved successfully")
                .data(permissions)
                .build());
    }

    @PostMapping
    @Operation(summary = "Create new permission")
    @PreAuthorize("hasAuthority('PERMISSION_CREATE')")
    public ResponseEntity<ResponseData<PermissionResponse>> createPermission(
            @Valid @RequestBody PermissionRequest request) {

        PermissionResponse permission = permissionService.createPermission(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<PermissionResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Permission created successfully")
                        .data(permission)
                        .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update permission")
    @PreAuthorize("hasAuthority('PERMISSION_UPDATE')")
    public ResponseEntity<ResponseData<PermissionResponse>> updatePermission(
            @Parameter(description = "Permission ID") @PathVariable Long id,
            @Valid @RequestBody PermissionRequest request) {

        PermissionResponse permission = permissionService.updatePermission(id, request);

        return ResponseEntity.ok(ResponseData.<PermissionResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Permission updated successfully")
                .data(permission)
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete permission")
    @PreAuthorize("hasAuthority('PERMISSION_DELETE')")
    public ResponseEntity<ResponseData<Void>> deletePermission(
            @Parameter(description = "Permission ID") @PathVariable Long id) {

        permissionService.deletePermission(id);

        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Permission deleted successfully")
                .build());
    }

    @GetMapping("/exists/{code}")
    @Operation(summary = "Check if permission code exists")
    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    public ResponseEntity<ResponseData<Boolean>> checkPermissionExists(
            @Parameter(description = "Permission code to check") @PathVariable String code) {

        boolean exists = permissionService.existsByCode(code);

        return ResponseEntity.ok(ResponseData.<Boolean>builder()
                .status(HttpStatus.OK.value())
                .message("Permission existence check completed")
                .data(exists)
                .build());
    }

    @GetMapping("/count")
    @Operation(summary = "Count total permissions")
    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    public ResponseEntity<ResponseData<Long>> countPermissions() {
        long count = permissionService.countPermissions();

        return ResponseEntity.ok(ResponseData.<Long>builder()
                .status(HttpStatus.OK.value())
                .message("Permissions count retrieved successfully")
                .data(count)
                .build());
    }
}
