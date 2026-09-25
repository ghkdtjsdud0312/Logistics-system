package com.logistics.global.event;

import java.time.Instant;

/** 상태 변경 이벤트. 감사로그와 대시보드가 소비한다. */
public record StatusChangedEvent(
        String eventId,
        String targetType,
        Long targetId,
        String targetNo,
        Long orderId,
        String action,
        String fromStatus,
        String toStatus,
        String actor,
        Instant occurredAt
) {
}
