package com.example.Backend.dtos.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;

    // User info
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private Boolean isActive;
    private LocalDateTime lastLoginAt;
    private Set<String> permissions;
}
