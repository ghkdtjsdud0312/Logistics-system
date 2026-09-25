package com.logistics.domain.inbound.domain;

/** 입고예정 → 입고완료 → 적치대기 → 적치완료 */
public enum InboundStatus {
    EXPECTED,
    RECEIVED,
    PUTAWAY_WAITING,
    PUTAWAY_DONE,
}
