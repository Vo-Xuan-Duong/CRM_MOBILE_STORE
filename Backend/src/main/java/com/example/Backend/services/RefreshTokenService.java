package com.example.Backend.services;

import com.example.Backend.models.RefreshToken;
import com.example.Backend.models.User;
import com.example.Backend.repositorys.RefreshTokenRepository;
import com.example.Backend.repositorys.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RefreshTokenService {

    @Value("${jwt.expiration.refresh}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshToken createRefreshToken(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            // Xóa refresh token cũ của user này
            deleteByUserId(userId);

            // Tạo refresh token mới
            RefreshToken refreshToken = RefreshToken.builder()
                    .id(UUID.randomUUID().toString())
                    .token(UUID.randomUUID().toString())
                    .user(user)
                    .expiryDate(LocalDateTime.now().plusSeconds(refreshTokenDurationMs / 1000))
                    .createdDate(LocalDateTime.now())
                    .build();

            return refreshTokenRepository.save(refreshToken);

        } catch (Exception e) {
            log.error("Error creating refresh token for user ID: {}", userId, e);
            throw new RuntimeException("Tạo refresh token thất bại: " + e.getMessage());
        }
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token đã hết hạn. Vui lòng đăng nhập lại");
        }
        return token;
    }

    public void deleteByUserId(Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                refreshTokenRepository.deleteByUser(user);
            }
        } catch (Exception e) {
            log.error("Error deleting refresh tokens for user ID: {}", userId, e);
        }
    }

    public RefreshToken createRefreshToken(String tokenId, String token) {
        RefreshToken refreshToken = RefreshToken.builder()
                .id(tokenId)
                .token(token)
                .createdDate(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusSeconds(refreshTokenDurationMs / 1000))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken getRefreshToken(String tokenId) {
        return refreshTokenRepository.findById(tokenId)
                .orElseThrow(() -> new RuntimeException("Refresh token không tồn tại"));
    }

    public void deleteRefreshToken(String tokenId) {
        try {
            refreshTokenRepository.deleteById(tokenId);
        } catch (Exception e) {
            log.error("Error deleting refresh token with ID: {}", tokenId, e);
        }
    }

    public void deleteAllRefreshTokensByUserId(String userId) {
        try {
            refreshTokenRepository.findAll().stream()
                    .filter(token -> token.getUser() != null &&
                            token.getUser().getId().toString().equals(userId))
                    .forEach(token -> refreshTokenRepository.deleteById(token.getId()));
        } catch (Exception e) {
            log.error("Error deleting all refresh tokens for user ID: {}", userId, e);
        }
    }

    // Cleanup expired tokens
    @Transactional
    public void deleteExpiredTokens() {
        try {
            refreshTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
            log.info("Cleaned up expired refresh tokens");
        } catch (Exception e) {
            log.error("Error cleaning up expired refresh tokens", e);
        }
    }
}
