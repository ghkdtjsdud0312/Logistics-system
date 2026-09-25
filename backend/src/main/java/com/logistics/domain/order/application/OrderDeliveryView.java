package com.logistics.domain.order.application;

import java.time.LocalDateTime;

/** 주문에 표시할 배송 정보. 배송 도메인이 채워 준다. */
public record OrderDeliveryView(
        String shipmentStatus,
        String vehicleNumber,
        String driverName,
        LocalDateTime plannedStartAt,
        LocalDateTime startedAt
) {
}
