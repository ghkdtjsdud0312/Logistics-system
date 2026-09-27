package com.logistics.domain.dispatch.domain;

/** 적재중 → 출발대기 → 배송중 → 배송 종료. 배송 시작 전에는 취소할 수 있다. */
public enum DispatchStatus {
    LOADING,
    REGISTERED,
    IN_TRANSIT,
    COMPLETED,
    CANCELLED,
}
