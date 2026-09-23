package com.logistics.domain.dispatch.application;

import com.logistics.domain.dispatch.domain.DispatchEventEnvelope;
import com.logistics.domain.dispatch.infrastructure.DispatchEventProducer;
import com.logistics.global.sse.LogisticsEventBroadcaster;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 배차 상태 변경이 실제로 커밋된 뒤에만 Kafka 발행 + SSE 브로드캐스트를 수행한다.
 * (트랜잭션이 롤백되면 이 리스너 자체가 호출되지 않는다 - "커밋 후 발행" 원칙)
 */
@Component
@RequiredArgsConstructor
public class DispatchEventRelay {

    private final DispatchEventProducer eventProducer;
    private final LogisticsEventBroadcaster broadcaster;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDispatchStatusChanged(DispatchStatusChangedEvent event) {
        String eventType = "DISPATCH_" + event.toStatus();
        DispatchEventEnvelope envelope = new DispatchEventEnvelope(
                UUID.randomUUID().toString(),
                eventType,
                String.valueOf(event.dispatchId()),
                event.version(),
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                Map.of("dispatchId", event.dispatchId(),
                        "fromStatus", event.fromStatus() == null ? "" : event.fromStatus().name(),
                        "toStatus", event.toStatus().name())
        );

        eventProducer.publish(envelope);
        broadcaster.broadcast(eventType, envelope);
    }
}
