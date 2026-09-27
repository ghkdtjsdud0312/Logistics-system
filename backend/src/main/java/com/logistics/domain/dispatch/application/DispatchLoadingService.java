package com.logistics.domain.dispatch.application;

import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.dispatch.domain.DispatchStatus;
import com.logistics.domain.loading.application.ShipmentService;
import com.logistics.domain.loading.domain.Shipment;
import com.logistics.domain.loading.domain.ShipmentStatus;
import com.logistics.domain.order.application.OrderShippingService;
import com.logistics.domain.vehicle.application.VehicleService;
import com.logistics.domain.vehicle.domain.CapacityValidator;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import com.logistics.global.event.StatusChangedEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/** 적재중(LOADING) 배차의 화물 추가·제외와 적재 마감. 변경마다 누적 중량을 검증한다. */
@Service
@RequiredArgsConstructor
@Transactional
public class DispatchLoadingService {

    private final DispatchService dispatchService;
    private final ShipmentService shipmentService;
    private final VehicleService vehicleService;
    private final OrderShippingService orderShippingService;
    private final DispatchWeightCalculator weightCalculator;
    private final StatusChangedEventPublisher eventPublisher;

    public Dispatch addShipments(Long dispatchId, List<Long> shipmentIds) {
        return attach(dispatchService.get(dispatchId), shipmentIds);
    }

    public Dispatch attach(Dispatch dispatch, List<Long> shipmentIds) {
        dispatch.requireLoading();
        List<Shipment> added = shipmentService.getAllForUpdate(shipmentIds);
        if (added.stream().anyMatch(s -> s.getStatus() != ShipmentStatus.LOADED || s.getDispatchId() != null)) {
            throw new BusinessException(ErrorCode.SHIPMENT_ALREADY_DISPATCHED);
        }
        List<Shipment> all = new ArrayList<>(shipmentService.getByDispatchId(dispatch.getId()));
        all.addAll(added);
        double weight = weightCalculator.of(all);
        if (!CapacityValidator.fits(vehicleService.getVehicle(dispatch.getVehicleId()), weight)) {
            throw new BusinessException(ErrorCode.VEHICLE_OVERLOAD);
        }
        int nextStop = all.stream().map(Shipment::getStopOrder).filter(Objects::nonNull)
                .mapToInt(Integer::intValue).max().orElse(0) + 1;
        Map<Long, Shipment> byId = added.stream().collect(Collectors.toMap(Shipment::getId, s -> s));
        for (Long id : new LinkedHashSet<>(shipmentIds)) {
            Shipment shipment = byId.get(id);
            shipment.assignTo(dispatch.getId());
            shipment.assignStopOrder(nextStop++);
            orderShippingService.markDispatched(shipment.getOrderId());
        }
        dispatch.updateWeight(weight);
        publish(dispatch, "ADD_SHIPMENT", DispatchStatus.LOADING, DispatchStatus.LOADING);
        return dispatch;
    }

    public Dispatch removeShipment(Long dispatchId, Long shipmentId) {
        Dispatch dispatch = dispatchService.get(dispatchId);
        dispatch.requireLoading();
        Shipment shipment = shipmentService.get(shipmentId);
        if (!dispatchId.equals(shipment.getDispatchId())) {
            throw new BusinessException(ErrorCode.SHIPMENT_NOT_FOUND);
        }
        shipment.unassign();
        orderShippingService.revertToLoaded(shipment.getOrderId());
        dispatch.updateWeight(weightCalculator.of(shipmentService.getByDispatchId(dispatchId)));
        publish(dispatch, "REMOVE_SHIPMENT", DispatchStatus.LOADING, DispatchStatus.LOADING);
        return dispatch;
    }

    public Dispatch close(Long dispatchId) {
        Dispatch dispatch = dispatchService.get(dispatchId);
        dispatch.requireLoading();
        if (shipmentService.getByDispatchId(dispatchId).isEmpty()) {
            throw new BusinessException(ErrorCode.DISPATCH_EMPTY);
        }
        dispatch.close();
        publish(dispatch, "CLOSE", DispatchStatus.LOADING, DispatchStatus.REGISTERED);
        return dispatch;
    }

    private void publish(Dispatch dispatch, String action, DispatchStatus from, DispatchStatus to) {
        eventPublisher.publish("DISPATCH", dispatch.getId(), dispatch.getDispatchNo(), null,
                action, from.name(), to.name());
    }
}
