package com.logistics.global.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/** 커밋된 상태 변경만 Kafka로 발행한다. 발행 실패는 업무 흐름을 막지 않고 로그만 남긴다. */
@Slf4j
@Component
@RequiredArgsConstructor
public class StatusChangedKafkaRelay {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void relay(StatusChangedEvent event) {
        try {
            kafkaTemplate.send(EventTopics.STATUS_CHANGED, event.targetType() + ":" + event.targetId(), event);
        } catch (RuntimeException e) {
            log.error("상태 변경 이벤트 발행 실패 eventId={} target={}:{}",
                    event.eventId(), event.targetType(), event.targetId(), e);
        }
    }
}
