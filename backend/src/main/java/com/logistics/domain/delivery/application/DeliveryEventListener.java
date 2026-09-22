package com.logistics.domain.delivery.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.domain.delivery.domain.DeliveryLocationEvent;
import com.logistics.domain.delivery.infrastructure.DeliverySseEmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * 배송 위치 이벤트 소비(Kafka Consumer) 후 SSE로 실시간 전달
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryEventListener {

    private final DeliverySseEmitterRepository sseEmitterRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "delivery-location-events", groupId = "logistics-system-delivery")
    public void onLocationEvent(DeliveryLocationEvent event) {
        SseEmitter emitter = sseEmitterRepository.get(event.deliveryId());
        if (emitter == null) {
            return;
        }
        try {
            emitter.send(SseEmitter.event()
                    .name("delivery-location")
                    .data(objectMapper.writeValueAsString(event)));
        } catch (IOException e) {
            log.warn("SSE 전송 실패. deliveryId={}", event.deliveryId(), e);
            emitter.completeWithError(e);
        }
    }
}
