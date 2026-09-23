package com.logistics.domain.anomaly;

import com.logistics.domain.anomaly.application.AnomalyService;
import com.logistics.domain.anomaly.domain.Anomaly;
import com.logistics.domain.anomaly.domain.AnomalyStatus;
import com.logistics.domain.anomaly.domain.AnomalyType;
import com.logistics.global.error.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class AnomalyServiceTest {

    @Autowired
    private AnomalyService anomalyService;

    @Test
    @DisplayName("같은 fingerprint의 OPEN 이상이 있으면 중복 기록하지 않는다")
    void record_suppressesDuplicateFingerprint() {
        anomalyService.record(AnomalyType.OVER_CAPACITY, 1L, "key-1", "첫 번째");
        anomalyService.record(AnomalyType.OVER_CAPACITY, 1L, "key-1", "두 번째(억제되어야 함)");

        long count = anomalyService.getAnomalies(AnomalyStatus.OPEN).stream()
                .filter(a -> a.getFingerprint().equals("OVER_CAPACITY:key-1"))
                .count();
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("OPEN -> ACKNOWLEDGED -> RESOLVED 순서로 상태를 변경할 수 있다")
    void changeStatus_fullLifecycle() {
        anomalyService.record(AnomalyType.DUPLICATE_ASSIGNMENT, null, "key-2", "메시지");
        Anomaly anomaly = anomalyService.getAnomalies(AnomalyStatus.OPEN).stream()
                .filter(a -> a.getFingerprint().equals("DUPLICATE_ASSIGNMENT:key-2"))
                .findFirst().orElseThrow();

        anomalyService.changeStatus(anomaly.getId(), AnomalyStatus.ACKNOWLEDGED);
        Anomaly resolved = anomalyService.changeStatus(anomaly.getId(), AnomalyStatus.RESOLVED);

        assertThat(resolved.getStatus()).isEqualTo(AnomalyStatus.RESOLVED);
    }

    @Test
    @DisplayName("단계를 건너뛰는 상태 전이는 거부된다")
    void changeStatus_skippingStage_throws() {
        anomalyService.record(AnomalyType.DRIVER_SCHEDULE_CONFLICT, null, "key-3", "메시지");
        Anomaly anomaly = anomalyService.getAnomalies(AnomalyStatus.OPEN).stream()
                .filter(a -> a.getFingerprint().equals("DRIVER_SCHEDULE_CONFLICT:key-3"))
                .findFirst().orElseThrow();

        assertThatThrownBy(() -> anomalyService.changeStatus(anomaly.getId(), AnomalyStatus.RESOLVED))
                .isInstanceOf(BusinessException.class);
    }
}
