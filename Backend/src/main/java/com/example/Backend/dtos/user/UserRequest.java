package com.example.Backend.dtos.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "Email must be valid")
    private String email;

    private String phone;

    private String role; // owner, manager, staff, tech, cs

    private Boolean isActive = true;

    @Size(min = 3, message = "Username must be at least 3 characters")
    private String username;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private Set<Long> roleIds;
}
