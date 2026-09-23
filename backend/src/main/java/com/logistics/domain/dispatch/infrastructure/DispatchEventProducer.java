package com.logistics.domain.dispatch.infrastructure;

import com.logistics.domain.dispatch.domain.DispatchEventEnvelope;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/** 배차 상태 변경 이벤트 발행 (Kafka Producer, 토픽 logistics.dispatch.v1) */
@Component
@RequiredArgsConstructor
public class DispatchEventProducer {

    public static final String TOPIC = "logistics.dispatch.v1";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(DispatchEventEnvelope envelope) {
        kafkaTemplate.send(TOPIC, envelope.aggregateId(), envelope);
    }
}
