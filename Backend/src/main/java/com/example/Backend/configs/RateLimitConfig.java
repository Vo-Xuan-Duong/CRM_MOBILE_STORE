package com.example.Backend.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Configuration
public class RateLimitConfig {

    @Bean
    public Map<String, Object> rateLimitCache() {
        return new ConcurrentHashMap<>();
    }
}
