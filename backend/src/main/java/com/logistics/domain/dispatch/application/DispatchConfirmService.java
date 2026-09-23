package com.logistics.domain.dispatch.application;

import com.logistics.domain.dispatch.domain.*;
import com.logistics.domain.dispatch.presentation.dto.DispatchConfirmRequest;
import com.logistics.domain.driver.application.DriverService;
import com.logistics.domain.driver.domain.Driver;
import com.logistics.domain.driver.domain.DriverStatus;
import com.logistics.domain.outbound.application.OutboundLoadSummary;
import com.logistics.domain.outbound.application.OutboundService;
import com.logistics.domain.vehicle.application.VehicleService;
import com.logistics.domain.vehicle.domain.CapacityValidator;
import com.logistics.domain.vehicle.domain.Vehicle;
import com.logistics.domain.vehicle.domain.VehicleStatus;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 배차 유스케이스 - 배차 확정 (TASK-006)
 * - 차량/기사 행은 비관적 잠금으로 먼저 확보해 동시 확정 시 하나만 통과하도록 한다.
 * - 출고 계획 중복 배정은 DB unique 제약(dispatch_outbound_link)으로 최종 차단한다.
 */
@Service
@RequiredArgsConstructor
public class DispatchConfirmService {

    private final OutboundService outboundService;
    private final VehicleService vehicleService;
    private final DriverService driverService;
    private final DispatchRepository dispatchRepository;

    @Transactional
    public Dispatch confirm(DispatchConfirmRequest request) {
        DispatchWindow window = DispatchWindow.of(request.plannedAt());
        validateOutboundsNotAssigned(request.outboundIds());
        OutboundLoadSummary load = outboundService.summarizeLoad(request.outboundIds());

        Vehicle vehicle = vehicleService.getVehicleForUpdate(request.vehicleId());
        validateVehicle(vehicle, load, window);

        Driver driver = driverService.getDriverForUpdate(request.driverId());
        validateDriver(driver, window);

        Dispatch dispatch = Dispatch.builder()
                .vehicleId(vehicle.getId())
                .driverId(driver.getId())
                .outboundIds(request.outboundIds())
                .plannedAt(request.plannedAt())
                .totalWeightKg(load.totalWeightKg())
                .totalVolumeM3(load.totalVolumeM3())
                .build();

        return saveOrRejectDuplicate(dispatch);
    }

    private void validateOutboundsNotAssigned(List<Long> outboundIds) {
        boolean alreadyAssigned = outboundIds.stream().anyMatch(dispatchRepository::existsByOutboundIdsContaining);
        if (alreadyAssigned) {
            throw new BusinessException(ErrorCode.DISPATCH_DUPLICATE_ASSIGNMENT);
        }
    }

    private void validateVehicle(Vehicle vehicle, OutboundLoadSummary load, DispatchWindow window) {
        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            throw new BusinessException(ErrorCode.DISPATCH_VEHICLE_UNAVAILABLE);
        }
        if (!CapacityValidator.fits(vehicle, load.totalWeightKg(), load.totalVolumeM3())) {
            throw new BusinessException(ErrorCode.DISPATCH_OVER_CAPACITY);
        }
        List<DispatchWindow> existing = dispatchRepository.findActiveByVehicleId(vehicle.getId()).stream()
                .map(Dispatch::window).toList();
        if (ScheduleConflictChecker.hasConflict(window, existing)) {
            throw new BusinessException(ErrorCode.DISPATCH_VEHICLE_UNAVAILABLE);
        }
    }

    private void validateDriver(Driver driver, DispatchWindow window) {
        if (driver.getStatus() != DriverStatus.AVAILABLE) {
            throw new BusinessException(ErrorCode.DISPATCH_DRIVER_SCHEDULE_CONFLICT);
        }
        List<DispatchWindow> existing = dispatchRepository.findActiveByDriverId(driver.getId()).stream()
                .map(Dispatch::window).toList();
        if (ScheduleConflictChecker.hasConflict(window, existing)) {
            throw new BusinessException(ErrorCode.DISPATCH_DRIVER_SCHEDULE_CONFLICT);
        }
    }

    private Dispatch saveOrRejectDuplicate(Dispatch dispatch) {
        try {
            return dispatchRepository.save(dispatch);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.DISPATCH_DUPLICATE_ASSIGNMENT);
        }
    }
}
