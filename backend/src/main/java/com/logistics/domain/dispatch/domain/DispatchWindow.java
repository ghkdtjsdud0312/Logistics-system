package com.logistics.domain.dispatch.domain;

import java.time.LocalDateTime;

/**
 * 기사 일정 충돌 판단용 시간 구간
 * - MVP 단순화: 배차 1건은 계획 시각부터 고정 길이(4시간)만큼 기사를 점유한다고 가정한다 (ADR 필요 시 DECISION_LOG 참고).
 */
public record DispatchWindow(LocalDateTime start, LocalDateTime end) {

    private static final long DEFAULT_DURATION_HOURS = 4;

    public static DispatchWindow of(LocalDateTime plannedAt) {
        return new DispatchWindow(plannedAt, plannedAt.plusHours(DEFAULT_DURATION_HOURS));
    }

    /** 경계가 맞닿는 경우([a,b)-[b,c))는 겹치지 않는 것으로 본다. */
    public boolean overlaps(DispatchWindow other) {
        return this.start.isBefore(other.end) && other.start.isBefore(this.end);
    }
}
