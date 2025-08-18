package com.example.Backend.mappers;

import com.example.Backend.dtos.user.UserRequest;
import com.example.Backend.dtos.user.UserResponse;
import com.example.Backend.models.Role;
import com.example.Backend.models.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    
    private final PasswordEncoder passwordEncoder;
    
    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
    public UserResponse toResponse(User user) {
        if (user == null) return null;
        
        Set<String> permissions = user.getRoles() != null ? 
            user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> permission.getCode())
                .collect(Collectors.toSet()) : Set.of();
        
        String roleNames = user.getRoles() != null ?
            user.getRoles().stream()
                .map(Role::getRole)
                .collect(Collectors.joining(", ")) : "";
        
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .username(user.getUsername())
                .role(roleNames)
                .isActive(user.getIsActive())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .permissions(permissions)
                .build();
    }
    
    public User toEntity(UserRequest request) {
        if (request == null) return null;
        
        return User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .username(request.getUsername())
                .passwordHash(request.getPassword() != null ? 
                    passwordEncoder.encode(request.getPassword()) : null)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
    }
    
    public void updateEntityFromRequest(User user, UserRequest request) {
        if (user == null || request == null) return;
        
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }
    }
}
