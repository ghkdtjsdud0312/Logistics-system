package com.logistics.domain.order.domain;

/**
 * 주문접수 → 출고대기 → 피킹중 → 피킹완료 → 포장완료 → 상차완료 → 배차완료 → 배송중 → 배송완료 | 배송실패
 * 배차 취소 시에만 배차완료에서 상차완료로 되돌릴 수 있다.
 */
public enum OrderStatus {
    RECEIVED,
    OUTBOUND_WAITING,
    PICKING,
    PICKED,
    PACKED,
    LOADED,
    DISPATCHED,
    IN_DELIVERY,
    DELIVERED,
    FAILED;

    public boolean canMoveTo(OrderStatus to) {
        return switch (this) {
            case RECEIVED -> to == OUTBOUND_WAITING;
            case OUTBOUND_WAITING -> to == PICKING;
            case PICKING -> to == PICKED;
            case PICKED -> to == PACKED;
            case PACKED -> to == LOADED;
            case LOADED -> to == DISPATCHED;
            case DISPATCHED -> to == IN_DELIVERY || to == LOADED;
            case IN_DELIVERY -> to == DELIVERED || to == FAILED;
            case DELIVERED, FAILED -> false;
        };
    }
}
