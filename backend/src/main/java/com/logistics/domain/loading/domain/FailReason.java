package com.logistics.domain.loading.domain;

/** 배송 실패 사유 */
public enum FailReason {
    CUSTOMER_ABSENT,
    ADDRESS_ERROR,
    REFUSED,
    DAMAGED,
    OTHER,
}
