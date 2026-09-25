package com.logistics.domain.warehouse.domain;

/** 피킹·포장 작업 상태. 포장 작업은 IN_PROGRESS 없이 WAITING → COMPLETED로 끝난다. */
public enum WorkStatus {
    WAITING,
    IN_PROGRESS,
    COMPLETED,
}
