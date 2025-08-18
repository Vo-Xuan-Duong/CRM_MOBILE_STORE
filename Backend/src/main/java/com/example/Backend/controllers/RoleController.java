package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.dtos.role.AddPermisstionRequest;
import com.example.Backend.dtos.role.RoleRequest;
import com.example.Backend.dtos.role.RoleResponse;
import com.example.Backend.services.RoleService;
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
@RequestMapping("/api/roles")
@Tag(name = "Role Management", description = "APIs for managing roles and permissions")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @Operation(summary = "Get all roles with pagination")
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<ResponseData<Page<RoleResponse>>> getAllRoles(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<RoleResponse> roles = roleService.getRolesPaginated(pageable);

        return ResponseEntity.ok(ResponseData.<Page<RoleResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Roles retrieved successfully")
                .data(roles)
                .build());
    }

    @GetMapping("/all")
    @Operation(summary = "Get all roles without pagination")
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<ResponseData<List<RoleResponse>>> getAllRolesNoPagination() {
        List<RoleResponse> roles = roleService.getAllRoles();

        return ResponseEntity.ok(ResponseData.<List<RoleResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("All roles retrieved successfully")
                .data(roles)
                .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get role by ID")
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<ResponseData<RoleResponse>> getRoleById(
            @Parameter(description = "Role ID") @PathVariable Long id) {

        RoleResponse role = roleService.getRoleById(id);

        return ResponseEntity.ok(ResponseData.<RoleResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Role retrieved successfully")
                .data(role)
                .build());
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get role by code")
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<ResponseData<RoleResponse>> getRoleByCode(
            @Parameter(description = "Role code") @PathVariable String code) {

        RoleResponse role = roleService.getRoleByCode(code);

        return ResponseEntity.ok(ResponseData.<RoleResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Role retrieved successfully")
                .data(role)
                .build());
    }

    @GetMapping("/permission/{permissionCode}")
    @Operation(summary = "Get roles by permission")
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<ResponseData<List<RoleResponse>>> getRolesByPermission(
            @Parameter(description = "Permission code") @PathVariable String permissionCode) {

        List<RoleResponse> roles = roleService.getRolesByPermission(permissionCode);

        return ResponseEntity.ok(ResponseData.<List<RoleResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Roles with permission " + permissionCode + " retrieved successfully")
                .data(roles)
                .build());
    }

    @PostMapping
    @Operation(summary = "Create new role")
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    public ResponseEntity<ResponseData<RoleResponse>> createRole(
            @Valid @RequestBody RoleRequest roleRequest) {

        RoleResponse role = roleService.createRole(roleRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<RoleResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Role created successfully")
                        .data(role)
                        .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update role")
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    public ResponseEntity<ResponseData<RoleResponse>> updateRole(
            @Parameter(description = "Role ID") @PathVariable Long id,
            @Valid @RequestBody RoleRequest roleRequest) {

        RoleResponse role = roleService.updateRole(id, roleRequest);

        return ResponseEntity.ok(ResponseData.<RoleResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Role updated successfully")
                .data(role)
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete role")
    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    public ResponseEntity<ResponseData<Void>> deleteRole(
            @Parameter(description = "Role ID") @PathVariable Long id) {

        roleService.deleteRole(id);

        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Role deleted successfully")
                .build());
    }

    @PostMapping("/{id}/permissions")
    @Operation(summary = "Add permissions to role")
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    public ResponseEntity<ResponseData<Void>> addPermissionsToRole(
            @Parameter(description = "Role ID") @PathVariable Long id,
            @Valid @RequestBody AddPermisstionRequest addPermissionRequest) {

        roleService.addPermissionsToRole(id, addPermissionRequest);

        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Permissions added to role successfully")
                .build());
    }

    @DeleteMapping("/{id}/permissions/{permissionCode}")
    @Operation(summary = "Remove permission from role")
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    public ResponseEntity<ResponseData<Void>> removePermissionFromRole(
            @Parameter(description = "Role ID") @PathVariable Long id,
            @Parameter(description = "Permission code") @PathVariable String permissionCode) {

        roleService.removePermissionFromRole(id, permissionCode);

        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Permission removed from role successfully")
                .build());
    }

    @GetMapping("/exists/{code}")
    @Operation(summary = "Check if role code exists")
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<ResponseData<Boolean>> checkRoleExists(
            @Parameter(description = "Role code to check") @PathVariable String code) {

        boolean exists = roleService.existsByCode(code);

        return ResponseEntity.ok(ResponseData.<Boolean>builder()
                .status(HttpStatus.OK.value())
                .message("Role existence check completed")
                .data(exists)
                .build());
    }

    @GetMapping("/count")
    @Operation(summary = "Count total roles")
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<ResponseData<Long>> countRoles() {
        long count = roleService.countRoles();

        return ResponseEntity.ok(ResponseData.<Long>builder()
                .status(HttpStatus.OK.value())
                .message("Roles count retrieved successfully")
                .data(count)
                .build());
    }
}
