package com.example.Backend.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlackListService {

    private final RedisService redisService;
    private static final String BLACKLIST_PREFIX = "blacklist:token:";
    private static final String USER_TOKENS_PREFIX = "user:tokens:";

    /**
     * Add token to blacklist
     */
    public void blacklistToken(String tokenId, long expirationTimeInSeconds) {
        try {
            String key = BLACKLIST_PREFIX + tokenId;
            redisService.set(key, "blacklisted", expirationTimeInSeconds, TimeUnit.SECONDS);
            log.info("Token {} added to blacklist for {} seconds", tokenId, expirationTimeInSeconds);
        } catch (Exception e) {
            log.error("Error blacklisting token {}: {}", tokenId, e.getMessage());
        }
    }

    /**
     * Check if token is blacklisted
     */
    public boolean isTokenBlacklisted(String tokenId) {
        try {
            String key = BLACKLIST_PREFIX + tokenId;
            boolean isBlacklisted = redisService.hasKey(key);
            log.debug("Token {} blacklist status: {}", tokenId, isBlacklisted);
            return isBlacklisted;
        } catch (Exception e) {
            log.error("Error checking blacklist status for token {}: {}", tokenId, e.getMessage());
            return false;
        }
    }

    /**
     * Remove token from blacklist (if needed)
     */
    public void removeFromBlacklist(String tokenId) {
        try {
            String key = BLACKLIST_PREFIX + tokenId;
            redisService.delete(key);
            log.info("Token {} removed from blacklist", tokenId);
        } catch (Exception e) {
            log.error("Error removing token {} from blacklist: {}", tokenId, e.getMessage());
        }
    }

    /**
     * Blacklist all tokens for a specific user
     */
    public void blacklistAllUserTokens(String username, long expirationTimeInSeconds) {
        try {
            String userTokensKey = USER_TOKENS_PREFIX + username;
            redisService.set(userTokensKey, "all_tokens_blacklisted", expirationTimeInSeconds, TimeUnit.SECONDS);
            log.info("All tokens for user {} blacklisted for {} seconds", username, expirationTimeInSeconds);
        } catch (Exception e) {
            log.error("Error blacklisting all tokens for user {}: {}", username, e.getMessage());
        }
    }

    /**
     * Check if all user tokens are blacklisted
     */
    public boolean areAllUserTokensBlacklisted(String username) {
        try {
            String userTokensKey = USER_TOKENS_PREFIX + username;
            boolean areBlacklisted = redisService.hasKey(userTokensKey);
            log.debug("All tokens for user {} blacklist status: {}", username, areBlacklisted);
            return areBlacklisted;
        } catch (Exception e) {
            log.error("Error checking if all tokens are blacklisted for user {}: {}", username, e.getMessage());
            return false;
        }
    }

    /**
     * Clear all blacklisted tokens for a user
     */
    public void clearUserTokenBlacklist(String username) {
        try {
            String userTokensKey = USER_TOKENS_PREFIX + username;
            redisService.delete(userTokensKey);
            log.info("Cleared all token blacklist for user {}", username);
        } catch (Exception e) {
            log.error("Error clearing token blacklist for user {}: {}", username, e.getMessage());
        }
    }

    /**
     * Store active token for user session management
     */
    public void storeActiveToken(String username, String tokenId, long expirationTimeInSeconds) {
        try {
            String key = "active:token:" + username;
            redisService.set(key, tokenId, expirationTimeInSeconds, TimeUnit.SECONDS);
            log.debug("Stored active token for user {}", username);
        } catch (Exception e) {
            log.error("Error storing active token for user {}: {}", username, e.getMessage());
        }
    }

    /**
     * Get active token for user
     */
    public String getActiveToken(String username) {
        try {
            String key = "active:token:" + username;
            Object token = redisService.get(key);
            return token != null ? token.toString() : null;
        } catch (Exception e) {
            log.error("Error getting active token for user {}: {}", username, e.getMessage());
            return null;
        }
    }

    /**
     * Remove active token for user
     */
    public void removeActiveToken(String username) {
        try {
            String key = "active:token:" + username;
            redisService.delete(key);
            log.debug("Removed active token for user {}", username);
        } catch (Exception e) {
            log.error("Error removing active token for user {}: {}", username, e.getMessage());
        }
    }
}
