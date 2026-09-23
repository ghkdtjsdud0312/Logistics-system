package com.logistics.domain.dispatch;

import com.logistics.domain.anomaly.application.AnomalyService;
import com.logistics.domain.anomaly.domain.AnomalyStatus;
import com.logistics.domain.anomaly.domain.AnomalyType;
import com.logistics.domain.dispatch.application.DispatchConfirmService;
import com.logistics.domain.dispatch.presentation.dto.DispatchConfirmRequest;
import com.logistics.domain.driver.domain.Driver;
import com.logistics.domain.vehicle.domain.Vehicle;
import com.logistics.global.error.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** 거부형 이상은 명령 실패와 함께 동기적으로 기록된다 (ADR-009) */
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class DispatchRejectionAnomalyTest {

    @Autowired
    private DispatchConfirmService dispatchConfirmService;
    @Autowired
    private DispatchTestFixtures fixtures;
    @Autowired
    private AnomalyService anomalyService;

    @Test
    @DisplayName("적재 초과로 배차가 거부되면 OVER_CAPACITY 이상이 즉시 기록된다")
    void confirm_overCapacity_recordsAnomaly() {
        Long outboundId = fixtures.createOutbound("서울", 900, 1.0);
        Vehicle vehicle = fixtures.createVehicle("00하0201", 500, 3.0, 10);
        Driver driver = fixtures.createDriver("이상기사1");
        String fingerprintKey = List.of(outboundId).toString();

        assertThatThrownBy(() -> dispatchConfirmService.confirm(new DispatchConfirmRequest(
                vehicle.getId(), driver.getId(), List.of(outboundId), LocalDateTime.now().plusHours(1))))
                .isInstanceOf(BusinessException.class);

        boolean recorded = anomalyService.getAnomalies(AnomalyStatus.OPEN).stream()
                .anyMatch(a -> a.getType() == AnomalyType.OVER_CAPACITY
                        && a.getFingerprint().equals("OVER_CAPACITY:" + fingerprintKey));
        assertThat(recorded).isTrue();
    }
}
