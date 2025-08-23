package com.example.Backend.controllers;

import com.example.Backend.dtos.ResponseData;
import com.example.Backend.dtos.auth.*;
import com.example.Backend.dtos.user.UserResponse;
import com.example.Backend.models.User;
import com.example.Backend.services.AuthService;
import com.example.Backend.services.UserService;
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
@Tag(name = "Authentication", description = "API xác thực và phân quyền")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

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

            authService.registerHandler(registerRequest);

            log.info("Registration successful for user: {}", registerRequest.getUsername());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseData.builder()
                            .status(HttpStatus.CREATED.value())
                            .message("Đăng ký tài khoản thành công , vui lòng xác thực otp để kich hoạt tài khoản")
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
    public ResponseEntity<ResponseData<?>> changePassword( HttpServletRequest request,
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        try {

            boolean isChanged = authService.changePasswordHandler(changePasswordRequest.getOldPassword(), changePasswordRequest.getNewPassword(), request);

            return ResponseEntity.ok(ResponseData.builder()
                    .status(HttpStatus.OK.value())
                    .message("Đổi mật khẩu thành công")
                    .data(isChanged)
                    .build());

        } catch (Exception e) {
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

    @PutMapping("/reset-password")
    @Operation(summary = "Đặt lại mật khẩu", description = "Đặt lại mật khẩu bằng token")
    public ResponseEntity<ResponseData<?>> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        try {
            boolean isReset = authService.resetPasswordHandler(resetPasswordRequest);
            return ResponseEntity.ok(ResponseData.builder()
                    .status(HttpStatus.OK.value())
                    .message("Đặt lại mật khẩu thành công")
                    .data(isReset)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ResponseData.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("Đặt lại mật khẩu thất bại: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Làm mới token", description = "Làm mới JWT token")
    public ResponseEntity<ResponseData<?>> refreshToken(@RequestParam String refreshToken) {
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

    @GetMapping("/user-info")
    @Operation(summary = "Thông tin người dùng", description = "Lấy thông tin người dùng hiện tại")
    public ResponseEntity<ResponseData<?>> getCurrentUserInfo() {
        try {

            User currentUser = authService.getCurrentUser();

            UserResponse userInfo = userService.getUserByUsername(currentUser.getUsername());

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
