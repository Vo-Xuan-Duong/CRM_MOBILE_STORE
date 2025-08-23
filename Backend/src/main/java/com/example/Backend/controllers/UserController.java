package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.dtos.user.UserRequest;
import com.example.Backend.dtos.user.UserResponse;
import com.example.Backend.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users with pagination")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Page<UserResponse>>> getAllUsers(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<UserResponse> users = userService.getUsersPaginated(pageable);

        return ResponseEntity.ok(ResponseData.<Page<UserResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Users retrieved successfully")
                .data(users)
                .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<ResponseData<UserResponse>> getUserById(
            @Parameter(description = "User ID") @PathVariable Long id) {

        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ResponseData.<UserResponse>builder()
                .status(HttpStatus.OK.value())
                .message("User retrieved successfully")
                .data(user)
                .build());
    }

    @PostMapping
    @Operation(summary = "Create new user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<UserResponse>> createUser(
            @Valid @RequestBody UserRequest userRequest) {

        log.info("Creating new user: {}", userRequest.getUsername());
        UserResponse createdUser = userService.createUser(userRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<UserResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("User created successfully")
                        .data(createdUser)
                        .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<ResponseData<UserResponse>> updateUser(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Valid @RequestBody UserRequest userRequest) {

        log.info("Updating user ID: {}", id);
        UserResponse updatedUser = userService.updateUser(id, userRequest);

        return ResponseEntity.ok(ResponseData.<UserResponse>builder()
                .status(HttpStatus.OK.value())
                .message("User updated successfully")
                .data(updatedUser)
                .build());
    }

    // ==================== DELETE, ACTIVATE, DEACTIVATE APIs ====================

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user permanently", description = "Permanently delete user from the system")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Void>> deleteUser(
            @Parameter(description = "User ID") @PathVariable Long id) {
        try {
            log.info("Deleting user ID: {}", id);
            userService.deleteUser(id);
            return ResponseEntity.ok(ResponseData.<Void>builder()
                    .status(HttpStatus.OK.value())
                    .message("User deleted successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error deleting user ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Void>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error deleting user: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/{id}/activate")
    @Operation(summary = "Activate user", description = "Activate a deactivated user account")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Void>> activateUser(
            @Parameter(description = "User ID") @PathVariable Long id) {
        try {
            log.info("Activating user ID: {}", id);
            userService.activateUser(id);
            return ResponseEntity.ok(ResponseData.<Void>builder()
                    .status(HttpStatus.OK.value())
                    .message("User activated successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error activating user ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Void>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error activating user: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate user", description = "Deactivate user account (soft delete)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Void>> deactivateUser(
            @Parameter(description = "User ID") @PathVariable Long id) {
        try {
            log.info("Deactivating user ID: {}", id);
            userService.deactivateUser(id);
            return ResponseEntity.ok(ResponseData.<Void>builder()
                    .status(HttpStatus.OK.value())
                    .message("User deactivated successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error deactivating user ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Void>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error deactivating user: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/active")
    @Operation(summary = "Get active users", description = "Get list of all active users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<List<UserResponse>>> getActiveUsers() {
        try {
            List<UserResponse> activeUsers = userService.getActiveUsers();
            return ResponseEntity.ok(ResponseData.<List<UserResponse>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Active users retrieved successfully")
                    .data(activeUsers)
                    .build());
        } catch (Exception e) {
            log.error("Error getting active users: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<List<UserResponse>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error getting active users: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/inactive")
    @Operation(summary = "Get inactive users", description = "Get list of all deactivated users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<List<UserResponse>>> getInactiveUsers() {
        try {
            List<UserResponse> inactiveUsers = userService.getInactiveUsers();
            return ResponseEntity.ok(ResponseData.<List<UserResponse>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Inactive users retrieved successfully")
                    .data(inactiveUsers)
                    .build());
        } catch (Exception e) {
            log.error("Error getting inactive users: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<List<UserResponse>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error getting inactive users: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Search users by username, email, or full name")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Page<UserResponse>>> searchUsers(
            @Parameter(description = "Search keyword") @RequestParam String keyword,
            Pageable pageable) {
        try {
            Page<UserResponse> users = userService.searchUsers(keyword, pageable);
            return ResponseEntity.ok(ResponseData.<Page<UserResponse>>builder()
                    .status(HttpStatus.OK.value())
                    .message("Users search completed successfully")
                    .data(users)
                    .build());
        } catch (Exception e) {
            log.error("Error searching users: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseData.<Page<UserResponse>>builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Error searching users: " + e.getMessage())
                            .build());
        }
    }
}
