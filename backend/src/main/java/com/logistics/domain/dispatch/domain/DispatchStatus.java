package com.logistics.domain.dispatch.domain;

public enum DispatchStatus {
    PLANNED,     // 배차 계획 수립됨
    OPTIMIZED,   // 경로 최적화 완료
    IN_TRANSIT,  // 배차 진행중
    COMPLETED,   // 배차 완료
}
