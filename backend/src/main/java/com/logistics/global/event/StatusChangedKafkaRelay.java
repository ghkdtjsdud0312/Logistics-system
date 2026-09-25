package com.logistics.global.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 커밋된 상태 변경만 Kafka로 발행한다. 발행 실패는 업무 흐름을 막지 않고 로그만 남긴다.
 * 테스트 프로파일에서는 app.event.kafka-enabled=false로 꺼서 실제 브로커에 이벤트가 쌓이지 않게 한다.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "app.event.kafka-enabled", havingValue = "true", matchIfMissing = true)
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
