package com.logistics.domain.audit.infrastructure;

import com.logistics.domain.audit.application.AuditService;
import com.logistics.global.event.EventTopics;
import com.logistics.global.event.StatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/** Kafka의 상태 변경 이벤트를 받아 감사로그에 기록한다. */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.event.kafka-enabled", havingValue = "true", matchIfMissing = true)
public class AuditConsumer {

    private final AuditService auditService;

    @KafkaListener(topics = EventTopics.STATUS_CHANGED, groupId = "logistics-audit")
    public void onMessage(StatusChangedEvent event) {
        if (!auditService.record(event)) {
            log.debug("이미 처리한 이벤트를 건너뜁니다. eventId={}", event.eventId());
        }
    }
}
