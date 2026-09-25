package com.logistics.domain.dashboard.infrastructure;

import com.logistics.domain.audit.application.AuditActionLabels;
import com.logistics.domain.dashboard.application.DashboardCache;
import com.logistics.domain.dashboard.presentation.dto.DashboardEvent;
import com.logistics.global.event.EventTopics;
import com.logistics.global.event.StatusChangedEvent;
import com.logistics.global.sse.LogisticsEventBroadcaster;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

/** 상태 변경 이벤트를 받아 대시보드 캐시를 비우고 SSE(status-changed)로 화면에 알린다. */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.event.kafka-enabled", havingValue = "true", matchIfMissing = true)
public class DashboardConsumer {

    static final String SSE_EVENT = "status-changed";

    private final DashboardCache cache;
    private final LogisticsEventBroadcaster broadcaster;

    @KafkaListener(topics = EventTopics.STATUS_CHANGED, groupId = "logistics-dashboard")
    public void onMessage(StatusChangedEvent event) {
        cache.evictAll();
        String description = event.targetNo() + " " + AuditActionLabels.of(event.targetType(), event.action());
        broadcaster.broadcast(SSE_EVENT,
                new DashboardEvent(LocalDateTime.ofInstant(event.occurredAt(), ZoneId.systemDefault()), description));
    }
}
