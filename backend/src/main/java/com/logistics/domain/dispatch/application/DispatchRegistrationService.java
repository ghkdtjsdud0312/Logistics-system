package com.logistics.domain.dispatch.application;

import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.dispatch.domain.DispatchRepository;
import com.logistics.domain.dispatch.domain.DispatchStatus;
import com.logistics.domain.driver.application.DriverService;
import com.logistics.domain.driver.domain.Driver;
import com.logistics.domain.driver.domain.DriverStatus;
import com.logistics.domain.master.application.WarehouseService;
import com.logistics.domain.vehicle.application.VehicleService;
import com.logistics.domain.vehicle.domain.Vehicle;
import com.logistics.domain.vehicle.domain.VehicleStatus;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import com.logistics.global.event.StatusChangedEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 배차 생성: 차량·기사를 검증해 적재중(LOADING) 배차를 만든다. 화물은 함께 담거나 나중에 담는다. */
@Service
@RequiredArgsConstructor
@Transactional
public class DispatchRegistrationService {

    private final DispatchRepository dispatchRepository;
    private final DispatchLoadingService loadingService;
    private final VehicleService vehicleService;
    private final DriverService driverService;
    private final WarehouseService warehouseService;
    private final StatusChangedEventPublisher eventPublisher;

    public Dispatch register(RegisterDispatchCommand cmd) {
        if (cmd.warehouseId() != null) {
            warehouseService.get(cmd.warehouseId());
        }
        Vehicle vehicle = vehicleService.getVehicleForUpdate(cmd.vehicleId());
        Driver driver = driverService.getDriverForUpdate(cmd.driverId());
        if (vehicle.getStatus() != VehicleStatus.AVAILABLE || dispatchRepository.existsActiveByVehicleId(vehicle.getId())) {
            throw new BusinessException(ErrorCode.VEHICLE_UNAVAILABLE);
        }
        if (driver.getStatus() != DriverStatus.AVAILABLE || dispatchRepository.existsActiveByDriverId(driver.getId())) {
            throw new BusinessException(ErrorCode.DRIVER_UNAVAILABLE);
        }
        Dispatch dispatch = dispatchRepository.save(new Dispatch(vehicle.getId(), driver.getId(),
                cmd.plannedStartAt(), cmd.plannedArrivalAt(), 0));
        dispatch.assignNo();
        dispatch.assignWarehouse(cmd.warehouseId());
        eventPublisher.publish("DISPATCH", dispatch.getId(), dispatch.getDispatchNo(), null,
                "CREATE", null, DispatchStatus.LOADING.name());
        if (cmd.shipmentIds() != null && !cmd.shipmentIds().isEmpty()) {
            loadingService.attach(dispatch, cmd.shipmentIds());
        }
        return dispatch;
    }
}
