package com.logistics.domain.anomaly.domain;

public enum AnomalyStatus {
    OPEN,
    ACKNOWLEDGED,
    RESOLVED;

    public boolean canTransitionTo(AnomalyStatus next) {
        return next.ordinal() == this.ordinal() + 1;
    }
}
