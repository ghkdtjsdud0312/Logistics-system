package com.logistics.domain.delivery.application;

import com.logistics.domain.loading.domain.FailReason;

/** 배송 실패가 확정되었을 때 발행하는 도메인 이벤트. 반품 도메인이 받아 반품을 만든다. */
public record ShipmentFailedEvent(Long shipmentId, Long orderId, FailReason reason, int quantity) {
}
