package com.logistics.domain.delivery.infrastructure;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 배송 실시간 관제(SSE) Emitter 저장소
 * - key: deliveryId, value: 연결된 클라이언트의 SseEmitter
 * - 다중 인스턴스 환경에서는 Redis Pub/Sub 등을 통한 브로드캐스트로 확장 필요
 */
@Component
public class DeliverySseEmitterRepository {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter save(Long deliveryId, SseEmitter emitter) {
        emitters.put(deliveryId, emitter);
        emitter.onCompletion(() -> emitters.remove(deliveryId));
        emitter.onTimeout(() -> emitters.remove(deliveryId));
        return emitter;
    }

    public SseEmitter get(Long deliveryId) {
        return emitters.get(deliveryId);
    }
}
