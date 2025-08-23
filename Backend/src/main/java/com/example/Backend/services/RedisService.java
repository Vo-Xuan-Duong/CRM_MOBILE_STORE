package com.example.Backend.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Set value with expiration time
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            log.debug("Successfully set key: {} with timeout: {} {}", key, timeout, unit);
        } catch (Exception e) {
            log.error("Error setting key: {} - {}", key, e.getMessage());
        }
    }

    /**
     * Set value without expiration
     */
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            log.debug("Successfully set key: {}", key);
        } catch (Exception e) {
            log.error("Error setting key: {} - {}", key, e.getMessage());
        }
    }

    /**
     * Get value by key
     */
    public Object get(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            log.debug("Retrieved value for key: {}", key);
            return value;
        } catch (Exception e) {
            log.error("Error getting key: {} - {}", key, e.getMessage());
            return null;
        }
    }

    /**
     * Delete key
     */
    public boolean delete(String key) {
        try {
            Boolean result = redisTemplate.delete(key);
            log.debug("Deleted key: {} - Result: {}", key, result);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error deleting key: {} - {}", key, e.getMessage());
            return false;
        }
    }

    /**
     * Check if key exists
     */
    public boolean hasKey(String key) {
        try {
            Boolean exists = redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.error("Error checking key existence: {} - {}", key, e.getMessage());
            return false;
        }
    }

    /**
     * Set expiration for existing key
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            Boolean result = redisTemplate.expire(key, timeout, unit);
            log.debug("Set expiration for key: {} - {} {} - Result: {}", key, timeout, unit, result);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error setting expiration for key: {} - {}", key, e.getMessage());
            return false;
        }
    }

    /**
     * Get remaining time to live for key
     */
    public long getExpire(String key) {
        try {
            return redisTemplate.getExpire(key);
        } catch (Exception e) {
            log.error("Error getting expiration for key: {} - {}", key, e.getMessage());
            return -1;
        }
    }

    /**
     * Get all keys matching pattern
     */
    public Set<String> keys(String pattern) {
        try {
            return redisTemplate.keys(pattern);
        } catch (Exception e) {
            log.error("Error getting keys with pattern: {} - {}", pattern, e.getMessage());
            return Set.of();
        }
    }

    /**
     * Increment value by delta
     */
    public Long increment(String key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            log.error("Error incrementing key: {} - {}", key, e.getMessage());
            return null;
        }
    }

    /**
     * Add to set
     */
    public Long addToSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error("Error adding to set: {} - {}", key, e.getMessage());
            return null;
        }
    }

    /**
     * Check if member exists in set
     */
    public boolean isMemberOfSet(String key, Object value) {
        try {
            Boolean result = redisTemplate.opsForSet().isMember(key, value);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error checking set membership: {} - {}", key, e.getMessage());
            return false;
        }
    }

    /**
     * Remove from set
     */
    public Long removeFromSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            log.error("Error removing from set: {} - {}", key, e.getMessage());
            return null;
        }
    }
}
