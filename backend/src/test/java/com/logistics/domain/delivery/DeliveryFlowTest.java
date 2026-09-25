package com.logistics.domain.delivery;

import com.logistics.domain.delivery.application.DeliveryService;
import com.logistics.domain.delivery.application.DeliveryStatusQueryService;
import com.logistics.domain.delivery.application.ShipmentFailedEvent;
import com.logistics.domain.delivery.presentation.dto.DeliveryStatusResponse;
import com.logistics.domain.dispatch.application.DispatchRegistrationService;
import com.logistics.domain.dispatch.application.DispatchService;
import com.logistics.domain.dispatch.application.RegisterDispatchCommand;
import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.dispatch.domain.DispatchStatus;
import com.logistics.domain.driver.application.DriverService;
import com.logistics.domain.driver.domain.DriverStatus;
import com.logistics.domain.loading.application.LoadingService;
import com.logistics.domain.loading.domain.FailReason;
import com.logistics.domain.loading.domain.Shipment;
import com.logistics.domain.loading.domain.ShipmentStatus;
import com.logistics.domain.order.application.OrderQueryService;
import com.logistics.domain.order.application.OrderService;
import com.logistics.domain.order.domain.OrderSearchCriteria;
import com.logistics.domain.order.domain.OrderStatus;
import com.logistics.domain.vehicle.application.VehicleService;
import com.logistics.domain.vehicle.domain.VehicleStatus;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import com.logistics.support.TestData;
import com.logistics.support.TestFlow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@RecordApplicationEvents
class DeliveryFlowTest {

    @Autowired private LoadingService loadingService;
    @Autowired private DispatchRegistrationService registrationService;
    @Autowired private DispatchService dispatchService;
    @Autowired private DeliveryService deliveryService;
    @Autowired private DeliveryStatusQueryService statusQueryService;
    @Autowired private OrderService orderService;
    @Autowired private OrderQueryService orderQueryService;
    @Autowired private VehicleService vehicleService;
    @Autowired private DriverService driverService;
    @Autowired private TestData testData;
    @Autowired private TestFlow testFlow;
    @Autowired private ApplicationEvents events;

    private Long vehicleId;
    private Long driverId;
    private Dispatch dispatch;
    private Shipment first;
    private Shipment second;

    @BeforeEach
    void setUp() {
        Long firstOrder = testFlow.packedOrder(0.5, 10);
        Long secondOrder = testFlow.packedOrder(0.5, 4);
        List<Shipment> shipments = loadingService.load(List.of(firstOrder, secondOrder));
        first = shipments.get(0);
        second = shipments.get(1);
        vehicleId = testData.vehicle(1000);
        driverId = testData.driver();
        dispatch = registrationService.register(new RegisterDispatchCommand(vehicleId, driverId,
                LocalDateTime.of(2026, 9, 25, 13, 0), LocalDateTime.of(2026, 9, 25, 15, 0),
                List.of(first.getId(), second.getId())));
        dispatchService.start(dispatch.getId());
    }

    private ErrorCode errorOf(Runnable action) {
        return catchThrowableOfType(action::run, BusinessException.class).getErrorCode();
    }

    @Test
    @DisplayName("인도수량이 배송수량과 같아야 완료되고 완료되면 주문도 배송완료·인도수량이 기록된다")
    void deliver() {
        assertThat(errorOf(() -> deliveryService.deliver(first.getId(), 9))).isEqualTo(ErrorCode.DELIVERED_QTY_MISMATCH);
        deliveryService.deliver(first.getId(), 10);

        assertThat(first.getStatus()).isEqualTo(ShipmentStatus.DELIVERED);
        assertThat(first.getDeliveredAt()).isNotNull();
        var order = orderService.get(first.getOrderId());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        assertThat(order.getItems().get(0).getDeliveredQty()).isEqualTo(10);
        assertThat(dispatch.getStatus()).isEqualTo(DispatchStatus.IN_TRANSIT);
        assertThat(errorOf(() -> deliveryService.deliver(first.getId(), 10))).isEqualTo(ErrorCode.SHIPMENT_NOT_IN_DELIVERY);
    }

    @Test
    @DisplayName("모든 배송이 완료·실패로 끝나면 배차가 완료되고 차량·기사가 운행가능으로 돌아온다")
    void completeDispatch() {
        deliveryService.deliver(first.getId(), 10);
        deliveryService.fail(second.getId(), FailReason.CUSTOMER_ABSENT, "문 앞 부재");

        assertThat(second.getStatus()).isEqualTo(ShipmentStatus.FAILED);
        assertThat(second.getFailReason()).isEqualTo(FailReason.CUSTOMER_ABSENT);
        assertThat(orderService.get(second.getOrderId()).getStatus()).isEqualTo(OrderStatus.FAILED);
        assertThat(dispatch.getStatus()).isEqualTo(DispatchStatus.COMPLETED);
        assertThat(vehicleService.getVehicle(vehicleId).getStatus()).isEqualTo(VehicleStatus.AVAILABLE);
        assertThat(driverService.getDriver(driverId).getStatus()).isEqualTo(DriverStatus.AVAILABLE);
        assertThat(statusQueryService.getBoard()).isEmpty();
    }

    @Test
    @DisplayName("배송 실패 시 반품이 만들어질 수 있도록 ShipmentFailedEvent를 발행하고 실패 후 재처리는 거부한다")
    void fail_publishesEvent() {
        deliveryService.fail(second.getId(), FailReason.DAMAGED, null);

        var failed = events.stream(ShipmentFailedEvent.class).toList();
        assertThat(failed).hasSize(1);
        assertThat(failed.get(0).reason()).isEqualTo(FailReason.DAMAGED);
        assertThat(failed.get(0).quantity()).isEqualTo(4);
        assertThat(errorOf(() -> deliveryService.fail(second.getId(), FailReason.OTHER, null)))
                .isEqualTo(ErrorCode.SHIPMENT_NOT_IN_DELIVERY);
    }

    @Test
    @DisplayName("배송현황은 차량별 완료/실패/전체 건수와 주문별 상태를 보여 주고 주문 조회에 배송 정보가 채워진다")
    void statusBoard_andOrderView() {
        deliveryService.deliver(first.getId(), 10);

        List<DeliveryStatusResponse> board = statusQueryService.getBoard();
        assertThat(board).hasSize(1);
        assertThat(board.get(0).completed()).isEqualTo(1);
        assertThat(board.get(0).failed()).isZero();
        assertThat(board.get(0).total()).isEqualTo(2);
        assertThat(board.get(0).orders()).extracting("status")
                .containsExactlyInAnyOrder(ShipmentStatus.DELIVERED, ShipmentStatus.IN_DELIVERY);

        var detail = orderQueryService.getDetail(second.getOrderId());
        assertThat(detail.delivery().driverName()).isEqualTo("홍길동");
        assertThat(detail.delivery().startedAt()).isNotNull();
        var rows = orderQueryService.search(new OrderSearchCriteria(null, null, OrderStatus.IN_DELIVERY, null, null, 0, 20));
        assertThat(rows).hasSize(1).first().extracting("deliveryStatus").isEqualTo("IN_DELIVERY");
    }

    @Test
    @DisplayName("배송 시작 전 Shipment는 완료·실패 처리할 수 없다")
    void notStarted() {
        Long order = testFlow.packedOrder(0.5, 3);
        Shipment waiting = loadingService.load(List.of(order)).get(0);

        assertThat(errorOf(() -> deliveryService.deliver(waiting.getId(), 3))).isEqualTo(ErrorCode.SHIPMENT_NOT_IN_DELIVERY);
        assertThat(errorOf(() -> deliveryService.fail(waiting.getId(), FailReason.OTHER, "x")))
                .isEqualTo(ErrorCode.SHIPMENT_NOT_IN_DELIVERY);
    }
}
