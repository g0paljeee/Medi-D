package com.example.Medico.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiter {

    private final Map<String, Bucket> rateLimiterBuckets = new ConcurrentHashMap<>();

    private final Bandwidth limit = Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));

    public boolean allowRequest(String key) {
        Bucket bucket = rateLimiterBuckets.computeIfAbsent(key, k -> Bucket4j.builder()
                .addLimit(limit)
                .build());
        return bucket.tryConsume(1);
    }
}
