package com.logistics.domain.delivery.application;

import com.logistics.domain.dispatch.application.DispatchService;
import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.driver.application.DriverService;
import com.logistics.domain.loading.application.ShipmentService;
import com.logistics.domain.loading.domain.Shipment;
import com.logistics.domain.order.application.OrderDeliveryProvider;
import com.logistics.domain.order.application.OrderDeliveryView;
import com.logistics.domain.vehicle.application.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** 주문 조회에 필요한 배송 정보(Shipment 상태, 차량·기사, 배차 시각)를 제공한다. */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShipmentOrderDeliveryProvider implements OrderDeliveryProvider {

    private final ShipmentService shipmentService;
    private final DispatchService dispatchService;
    private final VehicleService vehicleService;
    private final DriverService driverService;

    @Override
    public Map<Long, OrderDeliveryView> findByOrderIds(Collection<Long> orderIds) {
        Map<Long, OrderDeliveryView> views = new HashMap<>();
        if (orderIds.isEmpty()) {
            return views;
        }
        for (Shipment shipment : shipmentService.getByOrderIds(orderIds)) {
            views.put(shipment.getOrderId(), toView(shipment));
        }
        return views;
    }

    private OrderDeliveryView toView(Shipment shipment) {
        if (shipment.getDispatchId() == null) {
            return new OrderDeliveryView(shipment.getStatus().name(), null, null, null, null);
        }
        Dispatch dispatch = dispatchService.get(shipment.getDispatchId());
        return new OrderDeliveryView(shipment.getStatus().name(),
                vehicleService.getVehicle(dispatch.getVehicleId()).getVehicleNumber(),
                driverService.getDriver(dispatch.getDriverId()).getName(),
                dispatch.getPlannedStartAt(), dispatch.getStartedAt());
    }
}
