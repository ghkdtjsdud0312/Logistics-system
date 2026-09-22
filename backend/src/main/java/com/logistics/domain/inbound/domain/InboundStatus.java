package com.logistics.domain.inbound.domain;

/**
 * 입고 상태
 */
public enum InboundStatus {
    REQUESTED,   // 입고 요청
    IN_PROGRESS, // 입고 처리중
    COMPLETED,   // 입고 완료
    CANCELLED,   // 입고 취소
}
