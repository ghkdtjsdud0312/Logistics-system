package com.logistics.domain.dispatch;

import com.logistics.domain.dispatch.application.DispatchLoadingService;
import com.logistics.domain.dispatch.application.DispatchRegistrationService;
import com.logistics.domain.dispatch.application.DispatchService;
import com.logistics.domain.dispatch.application.RegisterDispatchCommand;
import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.dispatch.domain.DispatchStatus;
import com.logistics.domain.loading.application.LoadingService;
import com.logistics.domain.loading.domain.Shipment;
import com.logistics.domain.loading.domain.ShipmentStatus;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import com.logistics.support.TestData;
import com.logistics.support.TestFlow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class DispatchLoadingTest {

    @Autowired private DispatchRegistrationService registrationService;
    @Autowired private DispatchLoadingService loadingDispatchService;
    @Autowired private DispatchService dispatchService;
    @Autowired private LoadingService loadingService;
    @Autowired private TestData testData;
    @Autowired private TestFlow testFlow;

    private Shipment shipment(int qty) {
        return loadingService.load(List.of(testFlow.packedOrder(0.5, qty))).get(0);
    }

    private Dispatch emptyDispatch(double capacityKg) {
        return registrationService.register(new RegisterDispatchCommand(testData.vehicle(capacityKg),
                testData.driver(), LocalDateTime.now(), LocalDateTime.now().plusHours(2), null));
    }

    private ErrorCode errorOf(Runnable action) {
        return catchThrowableOfType(action::run, BusinessException.class).getErrorCode();
    }

    @Test
    @DisplayName("화물 없이 만든 배차에 화물을 나눠 담으면 중량이 누적되고 마감하면 출발대기가 된다")
    void fillAndClose() {
        Dispatch dispatch = emptyDispatch(1000);
        assertThat(dispatch.getStatus()).isEqualTo(DispatchStatus.LOADING);
        assertThat(dispatch.getTotalWeightKg()).isZero();

        loadingDispatchService.addShipments(dispatch.getId(), List.of(shipment(10).getId()));
        loadingDispatchService.addShipments(dispatch.getId(), List.of(shipment(4).getId()));
        assertThat(dispatch.getTotalWeightKg()).isEqualTo(7.0);

        loadingDispatchService.close(dispatch.getId());
        assertThat(dispatch.getStatus()).isEqualTo(DispatchStatus.REGISTERED);
    }

    @Test
    @DisplayName("담는 중 적재량을 초과하면 거부하고, 화물을 제외하면 중량이 줄고 Shipment는 상차완료로 돌아간다")
    void overloadAndRemove() {
        Dispatch dispatch = emptyDispatch(6);
        Shipment first = shipment(10);
        loadingDispatchService.addShipments(dispatch.getId(), List.of(first.getId()));
        Shipment second = shipment(4);
        assertThat(errorOf(() -> loadingDispatchService.addShipments(dispatch.getId(), List.of(second.getId()))))
                .isEqualTo(ErrorCode.VEHICLE_OVERLOAD);
        assertThat(second.getStatus()).isEqualTo(ShipmentStatus.LOADED);

        loadingDispatchService.removeShipment(dispatch.getId(), first.getId());
        assertThat(dispatch.getTotalWeightKg()).isZero();
        assertThat(first.getStatus()).isEqualTo(ShipmentStatus.LOADED);
        assertThat(first.getDispatchId()).isNull();
    }

    @Test
    @DisplayName("빈 배차는 마감할 수 없고, 마감 후에는 화물을 바꿀 수 없다")
    void closeRules() {
        Dispatch dispatch = emptyDispatch(1000);
        assertThat(errorOf(() -> loadingDispatchService.close(dispatch.getId()))).isEqualTo(ErrorCode.DISPATCH_EMPTY);

        Shipment shipment = shipment(10);
        loadingDispatchService.addShipments(dispatch.getId(), List.of(shipment.getId()));
        loadingDispatchService.close(dispatch.getId());
        assertThat(errorOf(() -> loadingDispatchService.addShipments(dispatch.getId(), List.of(shipment(2).getId()))))
                .isEqualTo(ErrorCode.DISPATCH_NOT_LOADING);
        assertThat(errorOf(() -> loadingDispatchService.removeShipment(dispatch.getId(), shipment.getId())))
                .isEqualTo(ErrorCode.DISPATCH_NOT_LOADING);
    }

    @Test
    @DisplayName("적재중 배차도 차량을 점유하고, 취소하면 담긴 화물이 풀리며 마감 전에는 출발할 수 없다")
    void occupyAndCancel() {
        Dispatch dispatch = emptyDispatch(1000);
        Shipment shipment = shipment(10);
        loadingDispatchService.addShipments(dispatch.getId(), List.of(shipment.getId()));
        assertThat(errorOf(() -> registrationService.register(new RegisterDispatchCommand(dispatch.getVehicleId(),
                testData.driver(), LocalDateTime.now(), LocalDateTime.now().plusHours(2), null))))
                .isEqualTo(ErrorCode.VEHICLE_UNAVAILABLE);
        assertThat(errorOf(() -> dispatchService.start(dispatch.getId()))).isEqualTo(ErrorCode.INVALID_DISPATCH_TRANSITION);

        dispatchService.cancel(dispatch.getId());
        assertThat(dispatch.getStatus()).isEqualTo(DispatchStatus.CANCELLED);
        assertThat(shipment.getStatus()).isEqualTo(ShipmentStatus.LOADED);
    }
}
