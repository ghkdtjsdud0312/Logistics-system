package com.logistics.domain.dispatch.application;

import com.logistics.domain.dispatch.domain.DispatchEventEnvelope;
import com.logistics.domain.dispatch.domain.DispatchStatus;
import com.logistics.domain.dispatch.domain.DispatchStatusProjection;
import com.logistics.domain.dispatch.domain.ProcessedEvent;
import com.logistics.domain.dispatch.domain.ProcessedEventRepository;
import com.logistics.domain.dispatch.infrastructure.DispatchEventProducer;
import com.logistics.domain.dispatch.infrastructure.DispatchStatusProjectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * History Consumer (TASK-009): 배차 상태 변경 이벤트를 멱등하게 소비해 조회용 투영을 갱신한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DispatchHistoryConsumer {

    private static final String CONSUMER_NAME = "history";

    private final ProcessedEventRepository processedEventRepository;
    private final DispatchStatusProjectionRepository projectionRepository;

    @Transactional
    @KafkaListener(topics = DispatchEventProducer.TOPIC, groupId = "logistics-history")
    public void onDispatchEvent(DispatchEventEnvelope envelope) {
        if (processedEventRepository.existsByConsumerNameAndEventId(CONSUMER_NAME, envelope.eventId())) {
            log.debug("이미 처리한 이벤트, 건너뜀. consumer={} eventId={}", CONSUMER_NAME, envelope.eventId());
            return;
        }

        Long dispatchId = Long.valueOf(envelope.aggregateId());
        DispatchStatus status = DispatchStatus.valueOf((String) envelope.payload().get("toStatus"));
        DispatchStatusProjection projection = projectionRepository.findById(dispatchId)
                .orElseGet(() -> DispatchStatusProjection.of(dispatchId, status, envelope.aggregateVersion(), envelope.occurredAt()));
        projection.apply(status, envelope.aggregateVersion(), envelope.occurredAt());
        projectionRepository.save(projection);

        processedEventRepository.save(ProcessedEvent.builder()
                .consumerName(CONSUMER_NAME)
                .eventId(envelope.eventId())
                .build());
    }
}
