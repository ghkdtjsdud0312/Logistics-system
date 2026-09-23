package com.logistics.global.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * 배차 상세 조회 cache-aside 설정 (Day 5)
 * - Key: dispatch:detail:{id}, TTL 기본 60초
 * - Redis 장애 시 예외를 삼켜(CacheErrorHandler) DB 폴백이 항상 동작하도록 한다 (ADR-010)
 */
@Slf4j
@EnableCaching
@Configuration
public class CacheConfig implements CachingConfigurer {

    @Value("${app.cache.dispatch-detail-ttl-seconds:60}")
    private long dispatchDetailTtlSeconds;

    /** 테스트 프로파일: Redis 없이도 캐시 히트/미스/무효화 로직을 검증하기 위한 인메모리 대체재 (ADR-010) */
    @Profile("test")
    @Bean("cacheManager")
    public CacheManager testCacheManager() {
        return new ConcurrentMapCacheManager("dispatchDetail");
    }

    @Profile("!test")
    @Bean("cacheManager")
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        // 앱 공용 ObjectMapper(JavaTimeModule 등 이미 등록됨)를 복사해 타입 정보(@class)만 추가로 활성화한다.
        // - GenericJackson2JsonRedisSerializer()의 무인자 생성자는 타입 정보는 넣어주지만 JavaTimeModule이
        //   없어 LocalDateTime 직렬화에서 예외가 나고, 공용 ObjectMapper를 그대로 쓰면 타입 정보가 없어
        //   역직렬화 시 DTO가 아닌 LinkedHashMap으로 읽혀 ClassCastException이 난다.
        ObjectMapper redisObjectMapper = objectMapper.copy();
        redisObjectMapper.activateDefaultTyping(
                redisObjectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        RedisCacheConfiguration dispatchDetailConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(dispatchDetailTtlSeconds))
                .disableCachingNullValues()
                .computePrefixWith(cacheName -> "dispatch:detail:")
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper)));

        return RedisCacheManager.builder(connectionFactory)
                .withCacheConfiguration("dispatchDetail", dispatchDetailConfig)
                .build();
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                log.warn("Redis 캐시 조회 실패, DB로 폴백한다. cache={} key={}", cache.getName(), key, exception);
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                log.warn("Redis 캐시 저장 실패, 캐시 없이 진행한다. cache={} key={}", cache.getName(), key, exception);
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                log.warn("Redis 캐시 무효화 실패. cache={} key={}", cache.getName(), key, exception);
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                log.warn("Redis 캐시 전체 삭제 실패. cache={}", cache.getName(), exception);
            }
        };
    }
}
