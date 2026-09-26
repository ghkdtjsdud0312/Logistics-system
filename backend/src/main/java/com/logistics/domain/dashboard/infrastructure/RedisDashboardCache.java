package com.logistics.domain.dashboard.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.domain.dashboard.application.DashboardCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

/** Redis에 JSON으로 저장하는 대시보드 캐시. Redis 장애 시 예외를 삼키고 DB 조회로 폴백하게 한다. */
@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class RedisDashboardCache implements DashboardCache {

    private static final String PREFIX = "dashboard:";

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    @Value("${app.cache.dashboard-ttl-seconds:30}")
    private long ttlSeconds;

    /** false면 캐시를 쓰지 않고 항상 DB에서 집계한다(성능 비교 측정용). */
    @Value("${app.cache.dashboard-enabled:true}")
    private boolean enabled;

    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        if (!enabled) {
            return Optional.empty();
        }
        try {
            String json = redis.opsForValue().get(PREFIX + key);
            return json == null ? Optional.empty() : Optional.of(objectMapper.readValue(json, type));
        } catch (RuntimeException | JsonProcessingException e) {
            log.warn("대시보드 캐시 조회 실패, DB로 폴백한다. key={}", key, e);
            return Optional.empty();
        }
    }

    @Override
    public void put(String key, Object value) {
        if (!enabled) {
            return;
        }
        try {
            redis.opsForValue().set(PREFIX + key, objectMapper.writeValueAsString(value), Duration.ofSeconds(ttlSeconds));
        } catch (RuntimeException | JsonProcessingException e) {
            log.warn("대시보드 캐시 저장 실패, 캐시 없이 진행한다. key={}", key, e);
        }
    }

    @Override
    public void evictAll() {
        try {
            Set<String> keys = redis.keys(PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                redis.delete(keys);
            }
        } catch (RuntimeException e) {
            log.warn("대시보드 캐시 무효화 실패", e);
        }
    }
}
