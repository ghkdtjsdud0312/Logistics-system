package com.logistics.domain.dispatch.application;

import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.dispatch.domain.DispatchStatus;
import com.logistics.domain.driver.application.DriverService;
import com.logistics.domain.driver.domain.DriverStatus;
import com.logistics.domain.loading.application.ShipmentService;
import com.logistics.domain.loading.domain.Shipment;
import com.logistics.domain.vehicle.application.VehicleService;
import com.logistics.domain.vehicle.domain.VehicleStatus;
import com.logistics.global.event.StatusChangedEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** 배차 안의 모든 Shipment가 종결되면 배차를 완료하고 차량·기사를 운행가능으로 되돌린다. */
@Service
@RequiredArgsConstructor
@Transactional
public class DispatchCompletionService {

    private final DispatchService dispatchService;
    private final ShipmentService shipmentService;
    private final VehicleService vehicleService;
    private final DriverService driverService;
    private final StatusChangedEventPublisher eventPublisher;

    public void completeIfAllClosed(Long dispatchId) {
        List<Shipment> shipments = shipmentService.getByDispatchId(dispatchId);
        if (shipments.isEmpty() || !shipments.stream().allMatch(Shipment::isClosed)) {
            return;
        }
        Dispatch dispatch = dispatchService.get(dispatchId);
        dispatch.complete();
        vehicleService.changeStatus(dispatch.getVehicleId(), VehicleStatus.AVAILABLE);
        driverService.changeStatus(dispatch.getDriverId(), DriverStatus.AVAILABLE);
        eventPublisher.publish("DISPATCH", dispatch.getId(), dispatch.getDispatchNo(), null,
                "COMPLETE", DispatchStatus.IN_TRANSIT.name(), DispatchStatus.COMPLETED.name());
    }
}
