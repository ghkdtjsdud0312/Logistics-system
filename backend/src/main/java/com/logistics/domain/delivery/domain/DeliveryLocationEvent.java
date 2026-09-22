package com.logistics.domain.delivery.domain;

/**
 * 배송 위치 변경 Kafka 이벤트 페이로드
 */
public record DeliveryLocationEvent(
        Long deliveryId,
        Long dispatchId,
        DeliveryStatus status,
        double latitude,
        double longitude
) {
}
