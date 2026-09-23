package com.logistics.domain.dispatch.domain;

public enum DispatchStatus {
    CONFIRMED,   // 차량/기사/물량 연결 완료 (배차 확정)
    LOADED,      // 상차 완료
    IN_TRANSIT,  // 배차 진행중
    COMPLETED;   // 배차 완료

    /** 단계 건너뛰기·역행 없이 바로 다음 단계로만 전이할 수 있다. */
    public boolean canTransitionTo(DispatchStatus next) {
        return next.ordinal() == this.ordinal() + 1;
    }
}
