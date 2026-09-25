package com.logistics.domain.returns.domain;

/** 회수요청 → 회수중 → 회수완료 → 반품입고 → 처리완료 */
public enum ReturnStatus {
    REQUESTED,
    COLLECTING,
    COLLECTED,
    RETURN_RECEIVED,
    COMPLETED,
}
