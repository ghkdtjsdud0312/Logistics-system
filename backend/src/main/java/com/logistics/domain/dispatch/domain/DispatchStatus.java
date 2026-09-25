package com.logistics.domain.dispatch.domain;

/** 배차완료 → 배송중 → 배송 종료. 배송 시작 전에는 취소할 수 있다. */
public enum DispatchStatus {
    REGISTERED,
    IN_TRANSIT,
    COMPLETED,
    CANCELLED,
}
