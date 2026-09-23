package com.logistics.domain.dispatch;

import com.logistics.domain.dispatch.domain.DispatchEventEnvelope;
import com.logistics.domain.dispatch.domain.DispatchStatus;
import com.logistics.domain.dispatch.infrastructure.DispatchEventProducer;
import com.logistics.domain.dispatch.infrastructure.DispatchStatusProjectionRepository;
import com.logistics.domain.dispatch.infrastructure.EventCursorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * HistoryConsumer/AnomalyConsumer가 실제 로컬 Kafka(docker-compose)를 통해 멱등/순서 보장을
 * 만족하는지 검증한다. 같은 이벤트를 두 번 보내도 결과는 한 번만 반영되어야 한다.
 */
@ActiveProfiles("test")
@SpringBootTest
class DispatchEventConsumerTest {

    @Autowired
    private DispatchEventProducer eventProducer;
    @Autowired
    private DispatchStatusProjectionRepository projectionRepository;
    @Autowired
    private EventCursorRepository eventCursorRepository;

    @Test
    @DisplayName("같은 이벤트를 두 번 보내도 투영/커서에는 한 번만 반영된다")
    void duplicateEvent_isProcessedOnce() {
        long dispatchId = 900_000 + System.nanoTime() % 100_000;
        String eventId = UUID.randomUUID().toString();
        DispatchEventEnvelope envelope = envelope(eventId, dispatchId, 1);

        eventProducer.publish(envelope);
        eventProducer.publish(envelope); // 동일 이벤트 재전송

        await().atMost(Duration.ofSeconds(15)).pollInterval(Duration.ofMillis(500)).untilAsserted(() -> {
            var projectionOpt = projectionRepository.findById(dispatchId);
            assertThat(projectionOpt).isPresent();
            assertThat(projectionOpt.get().getCurrentStatus()).isEqualTo(DispatchStatus.CONFIRMED);
            assertThat(projectionOpt.get().getLastEventVersion()).isEqualTo(1);
        });
    }

    @Test
    @DisplayName("역순으로 도착한 낮은 aggregateVersion 이벤트는 커서를 되돌리지 않는다")
    void outOfOrderEvent_doesNotRewindCursor() {
        long dispatchId = 800_000 + System.nanoTime() % 100_000;
        DispatchEventEnvelope v1 = envelope(UUID.randomUUID().toString(), dispatchId, 1);
        DispatchEventEnvelope v3 = envelope(UUID.randomUUID().toString(), dispatchId, 3);
        DispatchEventEnvelope v2 = envelope(UUID.randomUUID().toString(), dispatchId, 2); // 역순 도착

        eventProducer.publish(v1);
        eventProducer.publish(v3);
        eventProducer.publish(v2);

        await().atMost(Duration.ofSeconds(15)).pollInterval(Duration.ofMillis(500)).untilAsserted(() -> {
            var cursorOpt = eventCursorRepository.findByConsumerNameAndAggregateId("anomaly", String.valueOf(dispatchId));
            assertThat(cursorOpt).isPresent();
            assertThat(cursorOpt.get().getLastVersion()).isEqualTo(3);
        });
    }

    private DispatchEventEnvelope envelope(String eventId, long dispatchId, long version) {
        return new DispatchEventEnvelope(
                eventId, "DISPATCH_CONFIRMED", String.valueOf(dispatchId), version,
                LocalDateTime.now(), UUID.randomUUID().toString(),
                Map.of("dispatchId", dispatchId, "fromStatus", "", "toStatus", "CONFIRMED"));
    }
}
