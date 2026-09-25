package com.logistics.domain.dispatch;

import com.logistics.domain.dispatch.application.DispatchRegistrationService;
import com.logistics.domain.dispatch.application.DispatchService;
import com.logistics.domain.dispatch.application.RegisterDispatchCommand;
import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.dispatch.domain.DispatchStatus;
import com.logistics.domain.driver.application.DriverService;
import com.logistics.domain.driver.domain.DriverStatus;
import com.logistics.domain.loading.application.LoadingService;
import com.logistics.domain.loading.application.ShipmentService;
import com.logistics.domain.loading.domain.Shipment;
import com.logistics.domain.loading.domain.ShipmentStatus;
import com.logistics.domain.order.application.OrderService;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class DispatchFlowTest {

    @Autowired private LoadingService loadingService;
    @Autowired private ShipmentService shipmentService;
    @Autowired private DispatchRegistrationService registrationService;
    @Autowired private DispatchService dispatchService;
    @Autowired private OrderService orderService;
    @Autowired private VehicleService vehicleService;
    @Autowired private DriverService driverService;
    @Autowired private TestData testData;
    @Autowired private TestFlow testFlow;

    private Long orderId;
    private Shipment shipment;

    @BeforeEach
    void setUp() {
        orderId = testFlow.packedOrder(0.5, 10);
        shipment = loadingService.load(List.of(orderId)).get(0);
    }

    private ErrorCode errorOf(Runnable action) {
        return catchThrowableOfType(action::run, BusinessException.class).getErrorCode();
    }

    private Dispatch register(Long vehicleId, Long driverId) {
        return registrationService.register(new RegisterDispatchCommand(vehicleId, driverId,
                LocalDateTime.of(2026, 9, 25, 13, 0), LocalDateTime.of(2026, 9, 25, 15, 0), List.of(shipment.getId())));
    }

    @Test
    @DisplayName("상차하면 주문이 상차완료가 되고 Shipment가 만들어지며 포장 전 주문은 상차할 수 없다")
    void load() {
        assertThat(orderService.get(orderId).getStatus()).isEqualTo(OrderStatus.LOADED);
        assertThat(orderService.get(orderId).getItems().get(0).getLoadedQty()).isEqualTo(10);
        assertThat(shipment.getStatus()).isEqualTo(ShipmentStatus.LOADED);
        assertThat(errorOf(() -> loadingService.load(List.of(orderId)))).isEqualTo(ErrorCode.ORDER_NOT_PACKED);
    }

    @Test
    @DisplayName("적재량 이내면 배차가 등록되어 Shipment·주문이 배차완료가 되고 총 중량이 기록된다")
    void register_success() {
        Dispatch dispatch = register(testData.vehicle(1000), testData.driver());

        assertThat(dispatch.getDispatchNo()).isEqualTo("DSP-%03d".formatted(dispatch.getId()));
        assertThat(dispatch.getStatus()).isEqualTo(DispatchStatus.REGISTERED);
        assertThat(dispatch.getTotalWeightKg()).isEqualTo(5.0);
        assertThat(shipment.getStatus()).isEqualTo(ShipmentStatus.DISPATCHED);
        assertThat(orderService.get(orderId).getStatus()).isEqualTo(OrderStatus.DISPATCHED);
    }

    @Test
    @DisplayName("적재량과 같은 중량은 통과하고 초과하면 VEHICLE_OVERLOAD로 거부하며 배정은 바뀌지 않는다")
    void register_capacityBoundary() {
        assertThat(errorOf(() -> register(testData.vehicle(4.9), testData.driver()))).isEqualTo(ErrorCode.VEHICLE_OVERLOAD);
        assertThat(shipment.getStatus()).isEqualTo(ShipmentStatus.LOADED);
        assertThat(register(testData.vehicle(5.0), testData.driver()).getStatus()).isEqualTo(DispatchStatus.REGISTERED);
    }

    @Test
    @DisplayName("운행 불가 차량·기사, 활성 배차 중인 차량, 이미 배차된 Shipment는 거부한다")
    void register_unavailable() {
        Long maintenance = testData.vehicle(1000);
        vehicleService.changeStatus(maintenance, VehicleStatus.MAINTENANCE);
        Long off = testData.driver();
        driverService.changeStatus(off, DriverStatus.OFF);
        assertThat(errorOf(() -> register(maintenance, testData.driver()))).isEqualTo(ErrorCode.VEHICLE_UNAVAILABLE);
        assertThat(errorOf(() -> register(testData.vehicle(1000), off))).isEqualTo(ErrorCode.DRIVER_UNAVAILABLE);

        Long vehicle = testData.vehicle(1000);
        register(vehicle, testData.driver());
        assertThat(errorOf(() -> register(testData.vehicle(1000), testData.driver())))
                .isEqualTo(ErrorCode.SHIPMENT_ALREADY_DISPATCHED);
        Long secondOrder = testFlow.packedOrder(0.5, 2);
        Shipment second = loadingService.load(List.of(secondOrder)).get(0);
        assertThat(errorOf(() -> registrationService.register(new RegisterDispatchCommand(vehicle, testData.driver(),
                LocalDateTime.now(), LocalDateTime.now().plusHours(2), List.of(second.getId())))))
                .isEqualTo(ErrorCode.VEHICLE_UNAVAILABLE);
    }

    @Test
    @DisplayName("배송을 시작하면 Shipment·주문이 배송중이 되고 차량·기사가 운행중이 된다")
    void start() {
        Long vehicleId = testData.vehicle(1000);
        Long driverId = testData.driver();
        Dispatch dispatch = dispatchService.start(register(vehicleId, driverId).getId());

        assertThat(dispatch.getStatus()).isEqualTo(DispatchStatus.IN_TRANSIT);
        assertThat(dispatch.getStartedAt()).isNotNull();
        assertThat(shipment.getStatus()).isEqualTo(ShipmentStatus.IN_DELIVERY);
        assertThat(orderService.get(orderId).getStatus()).isEqualTo(OrderStatus.IN_DELIVERY);
        assertThat(vehicleService.getVehicle(vehicleId).getStatus()).isEqualTo(VehicleStatus.IN_OPERATION);
        assertThat(driverService.getDriver(driverId).getStatus()).isEqualTo(DriverStatus.DELIVERING);
        assertThat(errorOf(() -> dispatchService.start(dispatch.getId()))).isEqualTo(ErrorCode.INVALID_DISPATCH_TRANSITION);
    }

    @Test
    @DisplayName("배송 시작 전 취소하면 Shipment·주문이 상차완료로 되돌아가고 시작 후에는 취소할 수 없다")
    void cancel() {
        Dispatch dispatch = register(testData.vehicle(1000), testData.driver());
        dispatchService.cancel(dispatch.getId());

        assertThat(dispatch.getStatus()).isEqualTo(DispatchStatus.CANCELLED);
        assertThat(shipment.getStatus()).isEqualTo(ShipmentStatus.LOADED);
        assertThat(shipment.getDispatchId()).isNull();
        assertThat(orderService.get(orderId).getStatus()).isEqualTo(OrderStatus.LOADED);

        Dispatch again = register(testData.vehicle(1000), testData.driver());
        dispatchService.start(again.getId());
        assertThat(errorOf(() -> dispatchService.cancel(again.getId()))).isEqualTo(ErrorCode.INVALID_DISPATCH_TRANSITION);
        assertThat(shipmentService.getByDispatchId(again.getId())).hasSize(1);
    }
}
