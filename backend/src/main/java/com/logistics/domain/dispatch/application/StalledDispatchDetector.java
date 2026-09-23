package com.logistics.domain.dispatch.application;

import com.logistics.domain.anomaly.application.AnomalyService;
import com.logistics.domain.anomaly.domain.AnomalyType;
import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.dispatch.domain.DispatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * 지연형 이상 탐지 (TASK-009): IN_TRANSIT 상태가 기준 시간을 초과한 배차를 STALLED_DISPATCH로 기록한다.
 * 테스트 가능하도록 시각은 주입된 Clock을 사용한다.
 */
@Component
@RequiredArgsConstructor
public class StalledDispatchDetector {

    private final DispatchRepository dispatchRepository;
    private final AnomalyService anomalyService;
    private final Clock clock;

    @Value("${dispatch.stalled-threshold-minutes:240}")
    private long thresholdMinutes;

    @Scheduled(fixedRate = 5 * 60 * 1000L)
    public void detect() {
        LocalDateTime cutoff = LocalDateTime.now(clock).minusMinutes(thresholdMinutes);
        for (Dispatch dispatch : dispatchRepository.findStalledInTransit(cutoff)) {
            anomalyService.record(AnomalyType.STALLED_DISPATCH, dispatch.getId(), String.valueOf(dispatch.getId()),
                    "배차 #" + dispatch.getId() + "가 " + thresholdMinutes + "분 이상 IN_TRANSIT 상태입니다.");
        }
    }
}
