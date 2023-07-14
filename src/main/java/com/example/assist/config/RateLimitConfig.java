package com.example.assist.config;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfig {
    private static final double REQUESTS_PER_SECOND = 1;

    @Bean
    public RateLimiter rateLimiter() {
        return RateLimiter.create(REQUESTS_PER_SECOND);
    }
}