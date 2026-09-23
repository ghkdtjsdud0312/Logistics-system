package com.logistics.domain.dispatch.domain;

public enum RouteStopStatus {
    PENDING,
    ARRIVED,
    DELIVERED;

    /** 단계 건너뛰기·역행 없이 바로 다음 단계로만 전이할 수 있다. */
    public boolean canTransitionTo(RouteStopStatus next) {
        return next.ordinal() == this.ordinal() + 1;
    }
}
