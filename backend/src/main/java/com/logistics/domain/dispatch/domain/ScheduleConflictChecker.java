package com.logistics.domain.dispatch.domain;

import java.util.List;

/**
 * 기사 일정 겹침 판단 순수 함수
 */
public final class ScheduleConflictChecker {

    private ScheduleConflictChecker() {
    }

    public static boolean hasConflict(DispatchWindow candidate, List<DispatchWindow> existingWindows) {
        return existingWindows.stream().anyMatch(candidate::overlaps);
    }
}
