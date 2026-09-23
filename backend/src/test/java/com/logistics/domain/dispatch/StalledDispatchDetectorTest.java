package com.logistics.domain.dispatch;

import com.logistics.domain.anomaly.application.AnomalyService;
import com.logistics.domain.anomaly.domain.AnomalyStatus;
import com.logistics.domain.dispatch.application.DispatchConfirmService;
import com.logistics.domain.dispatch.application.DispatchStatusService;
import com.logistics.domain.dispatch.application.RouteOptimizationService;
import com.logistics.domain.dispatch.application.StalledDispatchDetector;
import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.dispatch.domain.DispatchRepository;
import com.logistics.domain.dispatch.domain.DispatchStatus;
import com.logistics.domain.dispatch.presentation.dto.DispatchConfirmRequest;
import com.logistics.domain.driver.domain.Driver;
import com.logistics.domain.vehicle.domain.Vehicle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class StalledDispatchDetectorTest {

    @Autowired
    private DispatchConfirmService dispatchConfirmService;
    @Autowired
    private DispatchStatusService dispatchStatusService;
    @Autowired
    private RouteOptimizationService routeOptimizationService;
    @Autowired
    private DispatchTestFixtures fixtures;
    @Autowired
    private AnomalyService anomalyService;
    @Autowired
    private DispatchRepository dispatchRepository;

    @Test
    @DisplayName("IN_TRANSIT가 기준 시간을 넘기면 STALLED_DISPATCH 이상을 기록한다")
    void detect_recordsStalledDispatch() {
        Long outboundId = fixtures.createOutbound("서울", 10, 0.1);
        Vehicle vehicle = fixtures.createVehicle("00하0099", 500, 3.0, 10);
        Driver driver = fixtures.createDriver("정체기사");
        Dispatch dispatch = dispatchConfirmService.confirm(new DispatchConfirmRequest(
                vehicle.getId(), driver.getId(), List.of(outboundId), LocalDateTime.now().plusHours(1)));
        dispatch = routeOptimizationService.optimize(dispatch.getId());
        dispatch = dispatchStatusService.changeStatus(dispatch.getId(), DispatchStatus.LOADED, dispatch.getVersion(), null, null);
        dispatch = dispatchStatusService.changeStatus(dispatch.getId(), DispatchStatus.IN_TRANSIT, dispatch.getVersion(), null, null);

        // IN_TRANSIT 진입 시각(updatedAt)으로부터 5시간 뒤를 "현재"로 만들어 정체 상태를 재현한다.
        Clock stalledClock = Clock.fixed(Instant.now().plusSeconds(5 * 3600), ZoneId.systemDefault());
        StalledDispatchDetector detector = new StalledDispatchDetector(dispatchRepository, anomalyService, stalledClock);
        detector.detect();

        Long dispatchId = dispatch.getId();
        boolean hasStalledAnomaly = anomalyService.getAnomalies(AnomalyStatus.OPEN).stream()
                .anyMatch(a -> a.getFingerprint().equals("STALLED_DISPATCH:" + dispatchId));
        assertThat(hasStalledAnomaly).isTrue();
    }
}
