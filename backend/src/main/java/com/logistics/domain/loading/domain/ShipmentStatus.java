package com.logistics.domain.loading.domain;

/** 상차완료 → 배차완료 → 배송중 → 배송완료 | 배송실패 */
public enum ShipmentStatus {
    LOADED,
    DISPATCHED,
    IN_DELIVERY,
    DELIVERED,
    FAILED,
}
