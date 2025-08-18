package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.dtos.user.UserRequest;
import com.example.Backend.dtos.user.UserResponse;
import com.example.Backend.services.UserService;
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
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get all users with pagination")
    @PreAuthorize("hasAuthority('USER_VIEW')")
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

    @GetMapping("/all")
    @Operation(summary = "Get all users without pagination")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public ResponseEntity<ResponseData<List<UserResponse>>> getAllUsersNoPagination() {
        List<UserResponse> users = userService.getAllUsers();

        return ResponseEntity.ok(ResponseData.<List<UserResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("All users retrieved successfully")
                .data(users)
                .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public ResponseEntity<ResponseData<UserResponse>> getUserById(
            @Parameter(description = "User ID") @PathVariable Long id) {

        UserResponse user = userService.getUserById(id);

        return ResponseEntity.ok(ResponseData.<UserResponse>builder()
                .status(HttpStatus.OK.value())
                .message("User retrieved successfully")
                .data(user)
                .build());
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Get user by username")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public ResponseEntity<ResponseData<UserResponse>> getUserByUsername(
            @Parameter(description = "Username") @PathVariable String username) {

        UserResponse user = userService.getUserByUsername(username);

        return ResponseEntity.ok(ResponseData.<UserResponse>builder()
                .status(HttpStatus.OK.value())
                .message("User retrieved successfully")
                .data(user)
                .build());
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active users")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public ResponseEntity<ResponseData<List<UserResponse>>> getActiveUsers() {
        List<UserResponse> users = userService.getActiveUsers();

        return ResponseEntity.ok(ResponseData.<List<UserResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Active users retrieved successfully")
                .data(users)
                .build());
    }

    @GetMapping("/role/{roleName}")
    @Operation(summary = "Get users by role")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public ResponseEntity<ResponseData<List<UserResponse>>> getUsersByRole(
            @Parameter(description = "Role name") @PathVariable String roleName) {

        List<UserResponse> users = userService.getUsersByRole(roleName);

        return ResponseEntity.ok(ResponseData.<List<UserResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Users with role " + roleName + " retrieved successfully")
                .data(users)
                .build());
    }

    @GetMapping("/search")
    @Operation(summary = "Search users by name")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public ResponseEntity<ResponseData<List<UserResponse>>> searchUsersByName(
            @Parameter(description = "Search term") @RequestParam String name) {

        List<UserResponse> users = userService.searchUsersByName(name);

        return ResponseEntity.ok(ResponseData.<List<UserResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Users matching '" + name + "' retrieved successfully")
                .data(users)
                .build());
    }

    @PostMapping
    @Operation(summary = "Create new user")
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public ResponseEntity<ResponseData<UserResponse>> createUser(
            @Valid @RequestBody UserRequest request) {

        UserResponse user = userService.createUser(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.<UserResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("User created successfully")
                        .data(user)
                        .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<ResponseData<UserResponse>> updateUser(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {

        UserResponse user = userService.updateUser(id, request);

        return ResponseEntity.ok(ResponseData.<UserResponse>builder()
                .status(HttpStatus.OK.value())
                .message("User updated successfully")
                .data(user)
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    public ResponseEntity<ResponseData<Void>> deleteUser(
            @Parameter(description = "User ID") @PathVariable Long id) {

        userService.deleteUser(id);

        return ResponseEntity.ok(ResponseData.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("User deleted successfully")
                .build());
    }

    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate user")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<ResponseData<UserResponse>> deactivateUser(
            @Parameter(description = "User ID") @PathVariable Long id) {

        UserResponse user = userService.deactivateUser(id);

        return ResponseEntity.ok(ResponseData.<UserResponse>builder()
                .status(HttpStatus.OK.value())
                .message("User deactivated successfully")
                .data(user)
                .build());
    }

    @PutMapping("/{id}/activate")
    @Operation(summary = "Activate user")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<ResponseData<UserResponse>> activateUser(
            @Parameter(description = "User ID") @PathVariable Long id) {

        UserResponse user = userService.activateUser(id);

        return ResponseEntity.ok(ResponseData.<UserResponse>builder()
                .status(HttpStatus.OK.value())
                .message("User activated successfully")
                .data(user)
                .build());
    }

    @GetMapping("/count/active")
    @Operation(summary = "Count active users")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public ResponseEntity<ResponseData<Long>> countActiveUsers() {
        long count = userService.countActiveUsers();

        return ResponseEntity.ok(ResponseData.<Long>builder()
                .status(HttpStatus.OK.value())
                .message("Active users count retrieved successfully")
                .data(count)
                .build());
    }

    @GetMapping("/exists/username/{username}")
    @Operation(summary = "Check if username exists")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public ResponseEntity<ResponseData<Boolean>> checkUsernameExists(
            @Parameter(description = "Username to check") @PathVariable String username) {

        boolean exists = userService.existsByUsername(username);

        return ResponseEntity.ok(ResponseData.<Boolean>builder()
                .status(HttpStatus.OK.value())
                .message("Username existence check completed")
                .data(exists)
                .build());
    }

    @GetMapping("/exists/email/{email}")
    @Operation(summary = "Check if email exists")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public ResponseEntity<ResponseData<Boolean>> checkEmailExists(
            @Parameter(description = "Email to check") @PathVariable String email) {

        boolean exists = userService.existsByEmail(email);

        return ResponseEntity.ok(ResponseData.<Boolean>builder()
                .status(HttpStatus.OK.value())
                .message("Email existence check completed")
                .data(exists)
                .build());
    }
}
