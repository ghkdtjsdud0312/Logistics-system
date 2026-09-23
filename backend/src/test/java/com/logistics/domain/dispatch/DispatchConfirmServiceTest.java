package com.logistics.domain.dispatch;

import com.logistics.domain.dispatch.application.DispatchConfirmService;
import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.dispatch.domain.DispatchStatus;
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
class DispatchConfirmServiceTest {

    @Autowired
    private DispatchConfirmService dispatchConfirmService;
    @Autowired
    private DispatchTestFixtures fixtures;

    @Test
    @Transactional
    @DisplayName("차량/기사/출고 계획을 연결해 배차를 확정한다")
    void confirm_success() {
        Long outboundId = fixtures.createOutbound("서울", 100, 1.0);
        Vehicle vehicle = fixtures.createVehicle("11가1111", 500, 3.0, 10);
        Driver driver = fixtures.createDriver("김기사");

        Dispatch dispatch = dispatchConfirmService.confirm(new DispatchConfirmRequest(
                vehicle.getId(), driver.getId(), List.of(outboundId), LocalDateTime.now().plusHours(1)));

        assertThat(dispatch.getId()).isNotNull();
        assertThat(dispatch.getStatus()).isEqualTo(DispatchStatus.CONFIRMED);
        assertThat(dispatch.getTotalWeightKg()).isEqualTo(100);
    }

    @Test
    @Transactional
    @DisplayName("적재 한도를 초과하는 차량은 배차를 확정할 수 없다")
    void confirm_overCapacity_throws() {
        Long outboundId = fixtures.createOutbound("서울", 900, 1.0);
        Vehicle vehicle = fixtures.createVehicle("22나2222", 500, 3.0, 10);
        Driver driver = fixtures.createDriver("이기사");

        assertThatThrownBy(() -> dispatchConfirmService.confirm(new DispatchConfirmRequest(
                vehicle.getId(), driver.getId(), List.of(outboundId), LocalDateTime.now().plusHours(1))))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.DISPATCH_OVER_CAPACITY);
    }

    @Test
    @Transactional
    @DisplayName("같은 출고 계획을 다른 배차에 다시 포함시킬 수 없다")
    void confirm_duplicateOutbound_throws() {
        Long outboundId = fixtures.createOutbound("서울", 100, 1.0);
        Vehicle vehicle1 = fixtures.createVehicle("33다3333", 500, 3.0, 10);
        Vehicle vehicle2 = fixtures.createVehicle("44라4444", 500, 3.0, 10);
        Driver driver1 = fixtures.createDriver("박기사");
        Driver driver2 = fixtures.createDriver("최기사");

        dispatchConfirmService.confirm(new DispatchConfirmRequest(
                vehicle1.getId(), driver1.getId(), List.of(outboundId), LocalDateTime.now().plusHours(1)));

        assertThatThrownBy(() -> dispatchConfirmService.confirm(new DispatchConfirmRequest(
                vehicle2.getId(), driver2.getId(), List.of(outboundId), LocalDateTime.now().plusHours(5))))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.DISPATCH_DUPLICATE_ASSIGNMENT);
    }

    @Test
    @Transactional
    @DisplayName("기사 일정이 겹치면 배차를 확정할 수 없지만, 경계가 맞닿는 경우는 허용한다")
    void confirm_driverScheduleConflict() {
        Long outbound1 = fixtures.createOutbound("서울", 100, 1.0);
        Long outbound2 = fixtures.createOutbound("인천", 100, 1.0);
        Long outbound3 = fixtures.createOutbound("수원", 100, 1.0);
        Vehicle vehicle1 = fixtures.createVehicle("55마5555", 500, 3.0, 10);
        Vehicle vehicle2 = fixtures.createVehicle("66바6666", 500, 3.0, 10);
        Vehicle vehicle3 = fixtures.createVehicle("77사7777", 500, 3.0, 10);
        Driver driver = fixtures.createDriver("정기사");
        LocalDateTime plannedAt = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);

        dispatchConfirmService.confirm(new DispatchConfirmRequest(
                vehicle1.getId(), driver.getId(), List.of(outbound1), plannedAt));

        // 겹치는 시간(2시간 뒤, 4시간 창 안): 충돌
        assertThatThrownBy(() -> dispatchConfirmService.confirm(new DispatchConfirmRequest(
                vehicle2.getId(), driver.getId(), List.of(outbound2), plannedAt.plusHours(2))))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.DISPATCH_DRIVER_SCHEDULE_CONFLICT);

        // 경계가 맞닿는 시간(정확히 4시간 뒤): 허용
        Dispatch boundaryDispatch = dispatchConfirmService.confirm(new DispatchConfirmRequest(
                vehicle3.getId(), driver.getId(), List.of(outbound3), plannedAt.plusHours(4)));
        assertThat(boundaryDispatch.getId()).isNotNull();
    }
}
