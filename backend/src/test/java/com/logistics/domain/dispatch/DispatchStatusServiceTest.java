package com.logistics.domain.dispatch;

import com.logistics.domain.dispatch.application.DispatchConfirmService;
import com.logistics.domain.dispatch.application.DispatchStatusService;
import com.logistics.domain.dispatch.application.RouteOptimizationService;
import com.logistics.domain.dispatch.application.RouteStopService;
import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.dispatch.domain.DispatchStatus;
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
class DispatchStatusServiceTest {

    @Autowired
    private DispatchConfirmService dispatchConfirmService;
    @Autowired
    private RouteOptimizationService routeOptimizationService;
    @Autowired
    private RouteStopService routeStopService;
    @Autowired
    private DispatchStatusService dispatchStatusService;
    @Autowired
    private DispatchTestFixtures fixtures;

    private Dispatch confirmDispatchWithRoute() {
        Long outboundId = fixtures.createOutbound("서울", 10, 0.1);
        Vehicle vehicle = fixtures.createVehicle("00하0002", 500, 3.0, 10);
        Driver driver = fixtures.createDriver("상태기사");
        Dispatch dispatch = dispatchConfirmService.confirm(new DispatchConfirmRequest(
                vehicle.getId(), driver.getId(), List.of(outboundId), LocalDateTime.now().plusHours(1)));
        return routeOptimizationService.optimize(dispatch.getId());
    }

    @Test
    @DisplayName("CONFIRMED -> LOADED -> IN_TRANSIT -> COMPLETED 순서로 전이할 수 있고 이력이 남는다")
    void changeStatus_fullLifecycle() {
        Dispatch dispatch = confirmDispatchWithRoute();
        dispatch.getStops().forEach(stop -> routeStopService.changeStopStatus(dispatch.getId(), stop.getId(), RouteStopStatus.ARRIVED));
        dispatch.getStops().forEach(stop -> routeStopService.changeStopStatus(dispatch.getId(), stop.getId(), RouteStopStatus.DELIVERED));

        Dispatch loaded = dispatchStatusService.changeStatus(dispatch.getId(), DispatchStatus.LOADED, dispatch.getVersion(), "김담당", "상차 완료");
        Dispatch inTransit = dispatchStatusService.changeStatus(dispatch.getId(), DispatchStatus.IN_TRANSIT, loaded.getVersion(), null, null);
        Dispatch completed = dispatchStatusService.changeStatus(
                dispatch.getId(), DispatchStatus.COMPLETED, inTransit.getVersion(), "김담당", null);

        assertThat(completed.getStatus()).isEqualTo(DispatchStatus.COMPLETED);
        assertThat(dispatchStatusService.getHistory(dispatch.getId())).hasSize(3);
    }

    @Test
    @DisplayName("단계를 건너뛰는 상태 전이는 거부된다")
    void changeStatus_skippingStage_throws() {
        Dispatch dispatch = confirmDispatchWithRoute();

        assertThatThrownBy(() -> dispatchStatusService.changeStatus(
                dispatch.getId(), DispatchStatus.IN_TRANSIT, dispatch.getVersion(), null, null))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.DISPATCH_INVALID_STATUS_TRANSITION);
    }

    @Test
    @DisplayName("경유지가 모두 배송 완료되지 않으면 COMPLETED로 전이할 수 없다")
    void changeStatus_toCompleted_withUndeliveredStops_throws() {
        Dispatch dispatch = confirmDispatchWithRoute();
        Dispatch loaded = dispatchStatusService.changeStatus(dispatch.getId(), DispatchStatus.LOADED, dispatch.getVersion(), null, null);
        Dispatch inTransit = dispatchStatusService.changeStatus(dispatch.getId(), DispatchStatus.IN_TRANSIT, loaded.getVersion(), null, null);

        assertThatThrownBy(() -> dispatchStatusService.changeStatus(
                dispatch.getId(), DispatchStatus.COMPLETED, inTransit.getVersion(), null, null))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.DISPATCH_STOPS_NOT_DELIVERED);
    }

    @Test
    @DisplayName("expectedVersion이 현재 버전과 다르면 거부된다")
    void changeStatus_staleVersion_throws() {
        Dispatch dispatch = confirmDispatchWithRoute();

        assertThatThrownBy(() -> dispatchStatusService.changeStatus(
                dispatch.getId(), DispatchStatus.LOADED, dispatch.getVersion() + 99, null, null))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.DISPATCH_STALE_VERSION);
    }
}
