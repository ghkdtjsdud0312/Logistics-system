package com.logistics.domain.dispatch.application;

import com.logistics.domain.dispatch.domain.DispatchEventEnvelope;
import com.logistics.domain.dispatch.domain.EventCursor;
import com.logistics.domain.dispatch.domain.ProcessedEvent;
import com.logistics.domain.dispatch.domain.ProcessedEventRepository;
import com.logistics.domain.dispatch.infrastructure.DispatchEventProducer;
import com.logistics.domain.dispatch.infrastructure.EventCursorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Anomaly Consumer (TASK-009): 같은 토픽을 독립된 그룹으로 소비하며 멱등 처리와 순서 보장을 담당한다.
 * 거부형 이상(OVER_CAPACITY 등)은 명령 실패 시점에 이미 동기로 기록되고(ADR-009), STALLED_DISPATCH는
 * 스케줄러가 기록하므로, 이 Consumer의 역할은 "역순 aggregateVersion 이벤트를 걸러내는" 멱등/순서 보장 그 자체다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DispatchAnomalyConsumer {

    private static final String CONSUMER_NAME = "anomaly";

    private final ProcessedEventRepository processedEventRepository;
    private final EventCursorRepository cursorRepository;

    @Transactional
    @KafkaListener(topics = DispatchEventProducer.TOPIC, groupId = "logistics-anomaly")
    public void onDispatchEvent(DispatchEventEnvelope envelope) {
        if (processedEventRepository.existsByConsumerNameAndEventId(CONSUMER_NAME, envelope.eventId())) {
            log.debug("이미 처리한 이벤트, 건너뜀. consumer={} eventId={}", CONSUMER_NAME, envelope.eventId());
            return;
        }

        EventCursor cursor = cursorRepository.findByConsumerNameAndAggregateId(CONSUMER_NAME, envelope.aggregateId())
                .orElseGet(() -> EventCursor.of(CONSUMER_NAME, envelope.aggregateId(), -1));
        if (envelope.aggregateVersion() <= cursor.getLastVersion()) {
            log.warn("역순 도착 이벤트, 건너뜀. aggregateId={} eventVersion={} lastVersion={}",
                    envelope.aggregateId(), envelope.aggregateVersion(), cursor.getLastVersion());
            processedEventRepository.save(ProcessedEvent.builder().consumerName(CONSUMER_NAME).eventId(envelope.eventId()).build());
            return;
        }

        cursor.advanceTo(envelope.aggregateVersion());
        cursorRepository.save(cursor);
        processedEventRepository.save(ProcessedEvent.builder()
                .consumerName(CONSUMER_NAME)
                .eventId(envelope.eventId())
                .build());
    }
}
