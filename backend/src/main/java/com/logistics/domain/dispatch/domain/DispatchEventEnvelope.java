package com.logistics.domain.dispatch.domain;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Kafka 이벤트 봉투 (토픽 logistics.dispatch.v1)
 */
public record DispatchEventEnvelope(
        String eventId,
        String eventType,
        String aggregateId,
        long aggregateVersion,
        LocalDateTime occurredAt,
        String traceId,
        Map<String, Object> payload
) {
}
