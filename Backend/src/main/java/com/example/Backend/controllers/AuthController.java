package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.dtos.auth.ChangePasswordRequest;
import com.example.Backend.dtos.auth.ForgotPasswordRequest;
import com.example.Backend.dtos.auth.LoginRequest;
import com.example.Backend.dtos.auth.RegisterRequest;
import com.example.Backend.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "API xác thực và phân quyền")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập hệ thống", description = "Đăng nhập cho nhân viên cửa hàng điện thoại")
    public ResponseEntity<ResponseData<?>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {
        try {
            log.info("Attempting login for user: {}", loginRequest.getUsername());

            Object loginData = authService.loginHandler(loginRequest);

            log.info("Login successful for user: {}", loginRequest.getUsername());

            return ResponseEntity.ok(ResponseData.builder()
                    .status(HttpStatus.OK.value())
                    .message("Đăng nhập thành công")
                    .data(loginData)
                    .build());

        } catch (Exception e) {
            log.error("Login failed for user: {}, error: {}", loginRequest.getUsername(), e.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseData.builder()
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .message("Đăng nhập thất bại: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Đăng ký tài khoản", description = "Đăng ký tài khoản nhân viên mới")
    public ResponseEntity<ResponseData<?>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            log.info("Attempting registration for user: {}", registerRequest.getUsername());

            Object registrationData = authService.registerHandler(registerRequest);

            log.info("Registration successful for user: {}", registerRequest.getUsername());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseData.builder()
                            .status(HttpStatus.CREATED.value())
                            .message("Đăng ký tài khoản thành công")
                            .data(registrationData)
                            .build());

        } catch (Exception e) {
            log.error("Registration failed for user: {}, error: {}", registerRequest.getUsername(), e.getMessage());

            return ResponseEntity.badRequest()
                    .body(ResponseData.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Đăng ký thất bại: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Đăng xuất", description = "Đăng xuất khỏi hệ thống")
    public ResponseEntity<ResponseData<?>> logout(
            HttpServletRequest request,
            Authentication authentication) {
        try {
            String username = authentication != null ? authentication.getName() : "unknown";
            log.info("Attempting logout for user: {}", username);

            boolean isLoggedOut = authService.logoutHandler(request);

            log.info("Logout successful for user: {}", username);

            return ResponseEntity.ok(ResponseData.builder()
                    .status(HttpStatus.OK.value())
                    .message("Đăng xuất thành công")
                    .data(isLoggedOut)
                    .build());

        } catch (Exception e) {
            log.error("Logout failed, error: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Đăng xuất thất bại: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/change-password")
    @Operation(summary = "Đổi mật khẩu", description = "Thay đổi mật khẩu tài khoản")
    public ResponseEntity<ResponseData<?>> changePassword(
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            log.info("Attempting password change for user: {}", username);

            boolean isChanged = authService.changePasswordHandler(
                    changePasswordRequest.getOldPassword(),
                    changePasswordRequest.getNewPassword()
            );

            log.info("Password changed successfully for user: {}", username);

            return ResponseEntity.ok(ResponseData.builder()
                    .status(HttpStatus.OK.value())
                    .message("Đổi mật khẩu thành công")
                    .data(isChanged)
                    .build());

        } catch (Exception e) {
            String username = authentication != null ? authentication.getName() : "unknown";
            log.error("Password change failed for user: {}, error: {}", username, e.getMessage());

            return ResponseEntity.badRequest()
                    .body(ResponseData.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Đổi mật khẩu thất bại: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Quên mật khẩu", description = "Gửi email reset mật khẩu")
    public ResponseEntity<ResponseData<?>> forgotPassword(@RequestParam ForgotPasswordRequest forgotPasswordRequest) {
        try {
            log.info("Attempting password reset for email: {}", forgotPasswordRequest.getEmail());

            boolean isEmailSent = authService.forgotPasswordHandler(forgotPasswordRequest.getEmail());

            log.info("Password reset email sent successfully for: {}", forgotPasswordRequest.getEmail());

            return ResponseEntity.ok(ResponseData.builder()
                    .status(HttpStatus.OK.value())
                    .message("Email reset mật khẩu đã được gửi")
                    .data(isEmailSent)
                    .build());

        } catch (Exception e) {
            log.error("Password reset failed for email: {}, error: {}", forgotPasswordRequest.getEmail(), e.getMessage());

            return ResponseEntity.badRequest()
                    .body(ResponseData.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Gửi email reset thất bại: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Làm mới token", description = "Làm mới JWT token")
    public ResponseEntity<ResponseData<?>> refreshToken(
            @RequestParam String refreshToken,
            HttpServletRequest request) {
        try {
            log.info("Attempting token refresh");

            Object newTokenData = authService.refreshTokenHandler(refreshToken);

            log.info("Token refreshed successfully");

            return ResponseEntity.ok(ResponseData.builder()
                    .status(HttpStatus.OK.value())
                    .message("Làm mới token thành công")
                    .data(newTokenData)
                    .build());

        } catch (Exception e) {
            log.error("Token refresh failed, error: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseData.builder()
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .message("Làm mới token thất bại: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/verify-token")
    @Operation(summary = "Kiểm tra token", description = "Kiểm tra tính hợp lệ của token")
    public ResponseEntity<ResponseData<?>> verifyToken(Authentication authentication) {
        try {
            if (authentication != null && authentication.isAuthenticated()) {
                return ResponseEntity.ok(ResponseData.builder()
                        .status(HttpStatus.OK.value())
                        .message("Token hợp lệ")
                        .data(true)
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ResponseData.builder()
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .message("Token không hợp lệ")
                                .data(false)
                                .build());
            }
        } catch (Exception e) {
            log.error("Token verification failed, error: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseData.builder()
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .message("Kiểm tra token thất bại: " + e.getMessage())
                            .data(false)
                            .build());
        }
    }

    @GetMapping("/user-info")
    @Operation(summary = "Thông tin người dùng", description = "Lấy thông tin người dùng hiện tại")
    public ResponseEntity<ResponseData<?>> getCurrentUserInfo(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ResponseData.builder()
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .message("Người dùng chưa đăng nhập")
                                .build());
            }

            String username = authentication.getName();
            Object userInfo = authService.getCurrentUserInfo(username);

            return ResponseEntity.ok(ResponseData.builder()
                    .status(HttpStatus.OK.value())
                    .message("Lấy thông tin người dùng thành công")
                    .data(userInfo)
                    .build());

        } catch (Exception e) {
            log.error("Get user info failed, error: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Lấy thông tin người dùng thất bại: " + e.getMessage())
                            .build());
        }
    }
}
