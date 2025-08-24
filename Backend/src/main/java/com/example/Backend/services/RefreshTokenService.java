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

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public void deleteByUserName(String username) {
        try {
            User user = userRepository.findByUsername(username).orElseThrow(
                    () -> new RuntimeException("User not found with username: " + username)
            );

            refreshTokenRepository.deleteByUser_Id(user.getId());

        } catch (Exception e) {
            log.error("Error deleting refresh tokens for username: {}", username, e);
        }
    }

    public String getIdTokenByRefreshToken(String refreshToken) {
        try {
            Optional<RefreshToken> tokenOptional = refreshTokenRepository.findByRefreshToken(refreshToken);
            if (tokenOptional.isPresent()) {
                return tokenOptional.get().getId();
            } else {
                log.warn("Refresh token not found: {}", refreshToken);
                return null;
            }
        } catch (Exception e) {
            log.error("Error retrieving ID token for refresh token: {}", refreshToken, e);
            return null;
        }
    }


    public void deleteRefreshToken(String tokenId) {
        try {
            Optional<RefreshToken> tokenOptional = refreshTokenRepository.findById(tokenId);
            if (tokenOptional.isPresent()) {
                refreshTokenRepository.deleteById(tokenId);
                log.info("Deleted refresh token with ID: {}", tokenId);
            } else {
                log.warn("Refresh token with ID: {} not found", tokenId);
            }
        } catch (Exception e) {
            log.error("Error deleting refresh token with ID: {}", tokenId, e);
        }
    }

    public void deleteAllRefreshTokensByUserId(Long userId) {
        try {
            refreshTokenRepository.findAll().stream()
                    .filter(token -> false)
                    .forEach(token -> refreshTokenRepository.deleteById(token.getId()));
        } catch (Exception e) {
            log.error("Error deleting all refresh tokens for user ID: {}", userId, e);
        }
    }

}
