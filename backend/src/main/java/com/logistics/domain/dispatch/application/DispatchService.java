package com.logistics.domain.dispatch.application;

import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.dispatch.domain.DispatchRepository;
import com.logistics.domain.dispatch.domain.DispatchStatus;
import com.logistics.domain.driver.application.DriverService;
import com.logistics.domain.driver.domain.DriverStatus;
import com.logistics.domain.loading.application.ShipmentService;
import com.logistics.domain.loading.domain.Shipment;
import com.logistics.domain.order.application.OrderShippingService;
import com.logistics.domain.vehicle.application.VehicleService;
import com.logistics.domain.vehicle.domain.VehicleStatus;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import com.logistics.global.event.StatusChangedEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

/** 배송 시작과 배차 취소. 차량·기사 상태와 Shipment·주문 상태를 함께 바꾼다. */
@Service
@RequiredArgsConstructor
@Transactional
public class DispatchService {

    private final DispatchRepository dispatchRepository;
    private final ShipmentService shipmentService;
    private final VehicleService vehicleService;
    private final DriverService driverService;
    private final OrderShippingService orderShippingService;
    private final StatusChangedEventPublisher eventPublisher;
    private final Clock clock;

    public Dispatch start(Long dispatchId) {
        Dispatch dispatch = get(dispatchId);
        dispatch.start(LocalDateTime.now(clock));
        if (vehicleService.getVehicle(dispatch.getVehicleId()).getStatus() != VehicleStatus.AVAILABLE) {
            throw new BusinessException(ErrorCode.VEHICLE_UNAVAILABLE);
        }
        if (driverService.getDriver(dispatch.getDriverId()).getStatus() != DriverStatus.AVAILABLE) {
            throw new BusinessException(ErrorCode.DRIVER_UNAVAILABLE);
        }
        for (Shipment shipment : shipmentService.getByDispatchId(dispatchId)) {
            shipment.startDelivery();
            orderShippingService.startDelivery(shipment.getOrderId());
        }
        vehicleService.changeStatus(dispatch.getVehicleId(), VehicleStatus.IN_OPERATION);
        driverService.changeStatus(dispatch.getDriverId(), DriverStatus.DELIVERING);
        publish(dispatch, "START", DispatchStatus.REGISTERED, DispatchStatus.IN_TRANSIT);
        return dispatch;
    }

    public Dispatch cancel(Long dispatchId) {
        Dispatch dispatch = get(dispatchId);
        dispatch.cancel();
        for (Shipment shipment : shipmentService.getByDispatchId(dispatchId)) {
            shipment.unassign();
            orderShippingService.revertToLoaded(shipment.getOrderId());
        }
        publish(dispatch, "CANCEL", DispatchStatus.REGISTERED, DispatchStatus.CANCELLED);
        return dispatch;
    }

    @Transactional(readOnly = true)
    public Dispatch get(Long id) {
        return dispatchRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DISPATCH_NOT_FOUND));
    }

    private void publish(Dispatch dispatch, String action, DispatchStatus from, DispatchStatus to) {
        eventPublisher.publish("DISPATCH", dispatch.getId(), dispatch.getDispatchNo(), null,
                action, from.name(), to.name());
    }
}
