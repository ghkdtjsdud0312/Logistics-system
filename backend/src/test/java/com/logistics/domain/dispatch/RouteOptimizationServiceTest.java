package com.logistics.domain.dispatch;

import com.logistics.domain.dispatch.application.DispatchConfirmService;
import com.logistics.domain.dispatch.application.RouteOptimizationService;
import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.dispatch.presentation.dto.DispatchConfirmRequest;
import com.logistics.domain.driver.domain.Driver;
import com.logistics.domain.vehicle.domain.Vehicle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class RouteOptimizationServiceTest {

    @Autowired
    private DispatchConfirmService dispatchConfirmService;
    @Autowired
    private RouteOptimizationService routeOptimizationService;
    @Autowired
    private DispatchTestFixtures fixtures;

    @Test
    @DisplayName("경로를 계산하면 허브가 첫 경유지이고 모든 출고 배송지를 한 번씩 포함한다")
    void optimize_includesHubAndAllOutboundsOnce() {
        Long outbound1 = fixtures.createOutbound("서울", 10, 0.1, 37.60, 127.10);
        Long outbound2 = fixtures.createOutbound("인천", 10, 0.1, 37.40, 126.90);
        Vehicle vehicle = fixtures.createVehicle("00하0001", 500, 3.0, 10);
        Driver driver = fixtures.createDriver("경로기사");

        Dispatch dispatch = dispatchConfirmService.confirm(new DispatchConfirmRequest(
                vehicle.getId(), driver.getId(), List.of(outbound1, outbound2), LocalDateTime.now().plusHours(1)));

        Dispatch optimized = routeOptimizationService.optimize(dispatch.getId());

        assertThat(optimized.getStops()).hasSize(3);
        assertThat(optimized.getStops().get(0).getOutboundId()).isNull();
        assertThat(optimized.getStops()).extracting("outboundId")
                .filteredOn(id -> id != null)
                .containsExactlyInAnyOrder(outbound1, outbound2);
        assertThat(optimized.getOptimizedDistanceKm()).isLessThanOrEqualTo(optimized.getInitialDistanceKm());
        assertThat(optimized.getStops().get(0).getDistanceFromPreviousKm()).isEqualTo(0.0);
    }
}
