package com.logistics.domain.dispatch;

import com.logistics.domain.dispatch.application.DispatchConfirmService;
import com.logistics.domain.dispatch.application.RouteOptimizationService;
import com.logistics.domain.dispatch.application.RouteStopService;
import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.dispatch.domain.RouteStop;
import com.logistics.domain.dispatch.domain.RouteStopStatus;
import com.logistics.domain.dispatch.presentation.dto.DispatchConfirmRequest;
import com.logistics.domain.driver.domain.Driver;
import com.logistics.domain.vehicle.domain.Vehicle;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
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

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class RouteStopServiceTest {

    @Autowired
    private DispatchConfirmService dispatchConfirmService;
    @Autowired
    private RouteOptimizationService routeOptimizationService;
    @Autowired
    private RouteStopService routeStopService;
    @Autowired
    private DispatchTestFixtures fixtures;

    private Dispatch confirmDispatchWithRoute() {
        Long outboundId = fixtures.createOutbound("서울", 10, 0.1);
        Vehicle vehicle = fixtures.createVehicle("00하0003", 500, 3.0, 10);
        Driver driver = fixtures.createDriver("경유지기사");
        Dispatch dispatch = dispatchConfirmService.confirm(new DispatchConfirmRequest(
                vehicle.getId(), driver.getId(), List.of(outboundId), LocalDateTime.now().plusHours(1)));
        return routeOptimizationService.optimize(dispatch.getId());
    }

    @Test
    @DisplayName("PENDING -> ARRIVED -> DELIVERED 순서로 전이할 수 있다")
    void changeStopStatus_fullLifecycle() {
        Dispatch dispatch = confirmDispatchWithRoute();
        Long stopId = dispatch.getStops().get(0).getId();

        routeStopService.changeStopStatus(dispatch.getId(), stopId, RouteStopStatus.ARRIVED);
        RouteStop delivered = routeStopService.changeStopStatus(dispatch.getId(), stopId, RouteStopStatus.DELIVERED);

        assertThat(delivered.getStatus()).isEqualTo(RouteStopStatus.DELIVERED);
    }

    @Test
    @DisplayName("단계를 건너뛰어 PENDING에서 바로 DELIVERED로 전이할 수 없다")
    void changeStopStatus_skippingStage_throws() {
        Dispatch dispatch = confirmDispatchWithRoute();
        Long stopId = dispatch.getStops().get(0).getId();

        assertThatThrownBy(() -> routeStopService.changeStopStatus(dispatch.getId(), stopId, RouteStopStatus.DELIVERED))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.ROUTE_STOP_INVALID_TRANSITION);
    }

    @Test
    @DisplayName("존재하지 않는 경유지 ID는 조회할 수 없다")
    void changeStopStatus_notFound_throws() {
        Dispatch dispatch = confirmDispatchWithRoute();

        assertThatThrownBy(() -> routeStopService.changeStopStatus(dispatch.getId(), 999_999L, RouteStopStatus.ARRIVED))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.ROUTE_STOP_NOT_FOUND);
    }
}
