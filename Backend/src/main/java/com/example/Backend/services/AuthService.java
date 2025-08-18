package com.example.Backend.services;

import com.example.Backend.dtos.auth.AuthResponse;
import com.example.Backend.dtos.auth.LoginRequest;
import com.example.Backend.dtos.auth.RegisterRequest;
import com.example.Backend.models.RefreshToken;
import com.example.Backend.models.Role;
import com.example.Backend.models.User;
import com.example.Backend.repositorys.RefreshTokenRepository;
import com.example.Backend.repositorys.RoleRepository;
import com.example.Backend.repositorys.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthResponse loginHandler(LoginRequest loginRequest) {
        try {
            // Xác thực người dùng
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Lấy thông tin user
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            // Tạo tokens
            String accessToken = jwtService.generateToken(user);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

            // Cập nhật last login
            user.setLastLoginDate(LocalDateTime.now());
            userRepository.save(user);

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getJwtExpiration())
                    .user(user)
                    .build();

        } catch (Exception e) {
            log.error("Login failed for user: {}", loginRequest.getUsername(), e);
            throw new RuntimeException("Đăng nhập thất bại: " + e.getMessage());
        }
    }

    public AuthResponse registerHandler(RegisterRequest registerRequest) {
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
            Role defaultRole = roleRepository.findByName("EMPLOYEE")
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy role mặc định"));

            // Tạo user mới
            User user = User.builder()
                    .username(registerRequest.getUsername())
                    .email(registerRequest.getEmail())
                    .fullName(registerRequest.getFullName())
                    .phoneNumber(registerRequest.getPhoneNumber())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .roles(Set.of(defaultRole))
                    .isEnabled(true)
                    .isAccountNonExpired(true)
                    .isAccountNonLocked(true)
                    .isCredentialsNonExpired(true)
                    .createdDate(LocalDateTime.now())
                    .build();

            User savedUser = userRepository.save(user);

            // Tạo tokens cho user mới
            String accessToken = jwtService.generateToken(savedUser);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser.getId());

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getJwtExpiration())
                    .user(savedUser)
                    .build();

        } catch (Exception e) {
            log.error("Registration failed for user: {}", registerRequest.getUsername(), e);
            throw new RuntimeException("Đăng ký thất bại: " + e.getMessage());
        }
    }

    public boolean logoutHandler(HttpServletRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null) {
                String username = authentication.getName();

                // Lấy token từ header
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);

                    // Vô hiệu hóa refresh tokens của user
                    User user = userRepository.findByUsername(username).orElse(null);
                    if (user != null) {
                        refreshTokenService.deleteByUserId(user.getId());
                    }
                }

                SecurityContextHolder.clearContext();
                return true;
            }
            return false;

        } catch (Exception e) {
            log.error("Logout failed", e);
            throw new RuntimeException("Đăng xuất thất bại: " + e.getMessage());
        }
    }

    public boolean changePasswordHandler(String oldPassword, String newPassword) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                throw new RuntimeException("Người dùng chưa đăng nhập");
            }

            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            // Kiểm tra mật khẩu cũ
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                throw new RuntimeException("Mật khẩu cũ không đúng");
            }

            // Cập nhật mật khẩu mới
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setPasswordChangedDate(LocalDateTime.now());
            userRepository.save(user);

            // Xóa tất cả refresh tokens để buộc đăng nhập lại
            refreshTokenService.deleteByUserId(user.getId());

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

            // Tạo reset token (có thể implement sau)
            String resetToken = jwtService.generatePasswordResetToken(user);

            // Lưu reset token vào database hoặc cache
            user.setPasswordResetToken(resetToken);
            user.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1)); // 1 giờ
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
            RefreshToken refreshTokenEntity = refreshTokenService.findByToken(refreshToken)
                    .orElseThrow(() -> new RuntimeException("Refresh token không hợp lệ"));

            refreshTokenService.verifyExpiration(refreshTokenEntity);

            User user = refreshTokenEntity.getUser();
            String newAccessToken = jwtService.generateToken(user);

            return AuthResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getJwtExpiration())
                    .user(user)
                    .build();

        } catch (Exception e) {
            log.error("Token refresh failed", e);
            throw new RuntimeException("Làm mới token thất bại: " + e.getMessage());
        }
    }

    public Object getCurrentUserInfo(String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            // Trả về thông tin user (không bao gồm password)
            return User.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .phoneNumber(user.getPhoneNumber())
                    .roles(user.getRoles())
                    .isEnabled(user.isEnabled())
                    .lastLoginDate(user.getLastLoginDate())
                    .createdDate(user.getCreatedDate())
                    .build();

        } catch (Exception e) {
            log.error("Get user info failed for username: {}", username, e);
            throw new RuntimeException("Lấy thông tin người dùng thất bại: " + e.getMessage());
        }
    }

    public boolean resetPasswordHandler(String token, String newPassword) {
        try {
            User user = userRepository.findByPasswordResetToken(token)
                    .orElseThrow(() -> new RuntimeException("Token reset không hợp lệ"));

            // Kiểm tra token còn hạn
            if (user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Token reset đã hết hạn");
            }

            // Cập nhật mật khẩu mới
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setPasswordResetToken(null);
            user.setPasswordResetTokenExpiry(null);
            user.setPasswordChangedDate(LocalDateTime.now());
            userRepository.save(user);

            // Xóa tất cả refresh tokens
            refreshTokenService.deleteByUserId(user.getId());

            return true;

        } catch (Exception e) {
            log.error("Reset password failed", e);
            throw new RuntimeException("Reset mật khẩu thất bại: " + e.getMessage());
        }
    }

    public boolean verifyEmailHandler(String token) {
        try {
            // Implement email verification logic
            // For now, just return true
            return true;
        } catch (Exception e) {
            log.error("Email verification failed", e);
            throw new RuntimeException("Xác thực email thất bại: " + e.getMessage());
        }
    }
}
