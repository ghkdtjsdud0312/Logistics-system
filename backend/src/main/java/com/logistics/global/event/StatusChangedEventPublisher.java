package com.logistics.global.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

/** 도메인 서비스가 트랜잭션 안에서 호출한다. 실제 Kafka 발행은 커밋 후에 일어난다. */
@Component
@RequiredArgsConstructor
public class StatusChangedEventPublisher {

    private final ApplicationEventPublisher publisher;
    private final ActorProvider actorProvider;
    private final Clock clock;

    public void publish(String targetType, Long targetId, String targetNo, Long orderId,
                        String action, String fromStatus, String toStatus) {
        publisher.publishEvent(new StatusChangedEvent(
                UUID.randomUUID().toString(), targetType, targetId, targetNo, orderId,
                action, fromStatus, toStatus, actorProvider.current(), Instant.now(clock)));
    }
}
