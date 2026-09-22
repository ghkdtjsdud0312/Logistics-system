package com.logistics.domain.delivery.infrastructure;

import com.logistics.domain.delivery.domain.DeliveryLocationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 배송 위치 이벤트 발행 (Kafka Producer)
 */
@Component
@RequiredArgsConstructor
public class DeliveryEventProducer {

    private static final String TOPIC = "delivery-location-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(DeliveryLocationEvent event) {
        kafkaTemplate.send(TOPIC, String.valueOf(event.deliveryId()), event);
    }
}
