package com.logistics.domain.dashboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.logistics.domain.dashboard.application.DashboardCache;
import com.logistics.domain.dashboard.infrastructure.DashboardConsumer;
import com.logistics.domain.dashboard.infrastructure.RedisDashboardCache;
import com.logistics.domain.dashboard.presentation.dto.DashboardEvent;
import com.logistics.domain.dashboard.presentation.dto.DashboardSummary;
import com.logistics.global.event.StatusChangedEvent;
import com.logistics.global.sse.LogisticsEventBroadcaster;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Redis 장애 시 폴백과 Kafka Consumer의 캐시 무효화·SSE 전송을 검증한다 (Redis·Kafka 없이). */
class DashboardCacheAndConsumerTest {

    @Test
    @DisplayName("Redis 연결이 실패해도 조회·저장·무효화가 예외 없이 빈 결과로 처리된다")
    void redisFailure_fallsBack() {
        StringRedisTemplate redis = mock(StringRedisTemplate.class);
        when(redis.opsForValue()).thenThrow(new RedisConnectionFailureException("connection refused"));
        when(redis.keys(any())).thenThrow(new RedisConnectionFailureException("connection refused"));
        RedisDashboardCache cache = new RedisDashboardCache(redis, new ObjectMapper().registerModule(new JavaTimeModule()));
        ReflectionTestUtils.setField(cache, "ttlSeconds", 30L);
        DashboardSummary summary = new DashboardSummary(LocalDate.now(), 1, 0, 0, 0, 0, 0, 0, Map.of());

        assertThat(cache.get("summary", DashboardSummary.class)).isEmpty();
        assertThatCode(() -> cache.put("summary", summary)).doesNotThrowAnyException();
        assertThatCode(cache::evictAll).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("상태 변경 이벤트를 받으면 캐시를 비우고 status-changed SSE로 한글 문구를 보낸다")
    void consumer_evictsAndBroadcasts() {
        DashboardCache cache = mock(DashboardCache.class);
        LogisticsEventBroadcaster broadcaster = mock(LogisticsEventBroadcaster.class);
        DashboardConsumer consumer = new DashboardConsumer(cache, broadcaster);

        consumer.onMessage(new StatusChangedEvent("e1", "ORDER", 1L, "ORD-001", 1L, "DELIVER",
                "IN_DELIVERY", "DELIVERED", "홍길동", Instant.now()));

        verify(cache).evictAll();
        ArgumentCaptor<Object> payload = ArgumentCaptor.forClass(Object.class);
        verify(broadcaster).broadcast(eq("status-changed"), payload.capture());
        assertThat(((DashboardEvent) payload.getValue()).description()).isEqualTo("ORD-001 배송 완료");
    }
}
