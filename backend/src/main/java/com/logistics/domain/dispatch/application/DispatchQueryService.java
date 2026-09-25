package com.logistics.domain.dispatch.application;

import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.dispatch.domain.DispatchRepository;
import com.logistics.domain.dispatch.domain.DispatchStatus;
import com.logistics.domain.dispatch.presentation.dto.DispatchResponse;
import com.logistics.domain.driver.application.DriverService;
import com.logistics.domain.driver.domain.Driver;
import com.logistics.domain.loading.application.ShipmentService;
import com.logistics.domain.loading.domain.Shipment;
import com.logistics.domain.vehicle.application.VehicleService;
import com.logistics.domain.vehicle.domain.Vehicle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** 배차 목록/상세 조회. 차량번호·기사명은 각 도메인 서비스로 조회한다. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DispatchQueryService {

    private final DispatchRepository dispatchRepository;
    private final DispatchService dispatchService;
    private final VehicleService vehicleService;
    private final DriverService driverService;
    private final ShipmentService shipmentService;

    public List<DispatchResponse> getList(DispatchStatus status) {
        return dispatchRepository.findAllByStatus(status).stream().map(this::toResponse).toList();
    }

    public DispatchResponse getDetail(Long id) {
        return toResponse(dispatchService.get(id));
    }

    public DispatchResponse toResponse(Dispatch dispatch) {
        Vehicle vehicle = vehicleService.getVehicle(dispatch.getVehicleId());
        Driver driver = driverService.getDriver(dispatch.getDriverId());
        List<Long> shipmentIds = shipmentService.getByDispatchId(dispatch.getId()).stream()
                .map(Shipment::getId).toList();
        return DispatchResponse.of(dispatch, vehicle, driver, shipmentIds);
    }
}
