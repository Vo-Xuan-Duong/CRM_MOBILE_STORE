package com.example.Backend.services;

import com.example.Backend.dtos.auth.AuthResponse;
import com.example.Backend.dtos.auth.LoginRequest;
import com.example.Backend.dtos.auth.RegisterRequest;
import com.example.Backend.dtos.auth.ResetPasswordRequest;
import com.example.Backend.enums.TokenType;
import com.example.Backend.models.Role;
import com.example.Backend.models.User;
import com.example.Backend.repositorys.RoleRepository;
import com.example.Backend.repositorys.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BlackListService blackListService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;
    private final UserService userService;

    public AuthResponse loginHandler(LoginRequest loginRequest) {
        try {
            // Xác thực người dùng
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());

            String idToken = UUID.randomUUID().toString();
            String accessToken = jwtService.generateToken(userDetails, idToken, TokenType.ACCESS_TOKEN);
            String refreshToken = jwtService.generateToken(userDetails, idToken, TokenType.REFRESH_TOKEN);

            // Cập nhật last login
            userService.updateLastLoginTime(loginRequest.getUsername());

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .user(userService.getUserByUsername(loginRequest.getUsername()))
                    .build();

        } catch (Exception e) {
            log.error("Login failed for user: {}", loginRequest.getUsername(), e);
            throw new RuntimeException("Đăng nhập thất bại: " + e.getMessage());
        }
    }

    public void registerHandler(RegisterRequest registerRequest) {
        try {
            // Kiểm tra username đã tồn tại
            if (userRepository.existsByUsername(registerRequest.getUsername())) {
                throw new RuntimeException("Tên đăng nhập đã tồn tại");
            }

            // Kiểm tra email đã tồn tại
            if (userRepository.existsByEmail(registerRequest.getEmail())) {
                throw new RuntimeException("Email đã tồn tại");
            }

            // Lấy role mặc định (EMPLOYEE)
            Role defaultRole = roleRepository.findByCode("EMPLOYEE")
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy role mặc định"));

            // Tạo user mới
            User user = User.builder()
                    .username(registerRequest.getUsername())
                    .email(registerRequest.getEmail())
                    .fullName(registerRequest.getFullName())
                    .phone(registerRequest.getPhone())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .roles(Set.of(defaultRole))
                    .isActive(false)
                    .build();

            User savedUser = userRepository.save(user);

            // Gửi email xác nhận đăng ký

        } catch (Exception e) {
            log.error("Registration failed for user: {}", registerRequest.getUsername(), e);
            throw new RuntimeException("Đăng ký thất bại: " + e.getMessage());
        }
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    public boolean logoutHandler(HttpServletRequest request) {
        try {
            User user = getCurrentUser();
            if (user != null) {
                String username = user.getUsername();

                String token = getTokenFromRequest(request);

                blackListService.addTokenToBlackList(token, 60);

                if (username != null) {
                    refreshTokenService.deleteByUserName(username);
                }
                log.info("Logout successful for user: {}", username);
                SecurityContextHolder.clearContext();
                return true;
            }
            return false;

        } catch (Exception e) {
            log.error("Logout failed", e);
            throw new RuntimeException("Đăng xuất thất bại: " + e.getMessage());
        }
    }

    public boolean changePasswordHandler(String oldPassword, String newPassword, HttpServletRequest request) {
        try {
            User user = getCurrentUser();

            // Kiểm tra mật khẩu cũ
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                throw new RuntimeException("Mật khẩu cũ không đúng");
            }
            // Cập nhật mật khẩu mới
            user.setPassword(passwordEncoder.encode(newPassword));

            userRepository.save(user);

            log.info("Change password successful for user: {}", user.getUsername());

            logoutHandler(request);

            return true;

        } catch (Exception e) {
            log.error("Password change failed", e);
            throw new RuntimeException("Đổi mật khẩu thất bại: " + e.getMessage());
        }
    }

    public boolean forgotPasswordHandler(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy email trong hệ thống"));

            userRepository.save(user);

            // Gửi email (implement sau)
            log.info("Password reset token generated for user: {}", email);

            return true;

        } catch (Exception e) {
            log.error("Forgot password failed for email: {}", email, e);
            throw new RuntimeException("Gửi email reset mật khẩu thất bại: " + e.getMessage());
        }
    }

    public AuthResponse refreshTokenHandler(String refreshToken) {
        try {
            if( refreshToken == null || refreshToken.isEmpty()) {
                throw new RuntimeException("Refresh token không được để trống");
            }
            if(jwtService.verifyToken(refreshToken, TokenType.REFRESH_TOKEN)){
                throw new RuntimeException("Refresh token không hợp lệ hoặc đã hết hạn");
            }

            String username = jwtService.extractName(refreshToken, TokenType.REFRESH_TOKEN);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            String newAccessToken = jwtService.generateToken(userDetails, refreshTokenService.getIdTokenByRefreshToken(refreshToken), TokenType.ACCESS_TOKEN);

            return AuthResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken)
                    .user(userService.getUserByUsername(username))
                    .build();

        } catch (Exception e) {
            log.error("Token refresh failed", e);
            throw new RuntimeException("Làm mới token thất bại: " + e.getMessage());
        }
    }

    public boolean resetPasswordHandler(ResetPasswordRequest resetPasswordRequest) {
        try {
            User user = getCurrentUser();
            // Cập nhật mật khẩu mới
            if (resetPasswordRequest.getNewPassword() == null || resetPasswordRequest.getNewPassword().isEmpty()) {
                throw new RuntimeException("Mật khẩu mới không được để trống");
            }
            if (!resetPasswordRequest.isPasswordMatching()) {
                throw new RuntimeException("Mật khẩu mới và xác nhận mật khẩu không khớp");
            }
            user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
            userRepository.save(user);

            // Xóa tất cả refresh tokens
            refreshTokenService.deleteByUserName(user.getUsername());

            return true;

        } catch (Exception e) {
            log.error("Reset password failed", e);
            throw new RuntimeException("Reset mật khẩu thất bại: " + e.getMessage());
        }
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Người dùng chưa đăng nhập");
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + username));
    }




}
