package com.logistics.global.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.data.redis.RedisConnectionFailureException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Redis 장애 시 캐시 오류를 삼켜 DB 폴백이 가능하게 하는지 검증 (ADR-010) */
class CacheConfigTest {

    private final CacheErrorHandler errorHandler = new CacheConfig().errorHandler();

    @Test
    @DisplayName("캐시 조회/저장/무효화/전체삭제 오류는 예외를 던지지 않고 삼킨다")
    void allCacheOperations_swallowErrors() {
        Cache cache = mock(Cache.class);
        when(cache.getName()).thenReturn("dashboard");
        RedisConnectionFailureException exception = new RedisConnectionFailureException("connection refused");

        assertThatCode(() -> errorHandler.handleCacheGetError(exception, cache, "1")).doesNotThrowAnyException();
        assertThatCode(() -> errorHandler.handleCachePutError(exception, cache, "1", "value")).doesNotThrowAnyException();
        assertThatCode(() -> errorHandler.handleCacheEvictError(exception, cache, "1")).doesNotThrowAnyException();
        assertThatCode(() -> errorHandler.handleCacheClearError(exception, cache)).doesNotThrowAnyException();
    }
}
