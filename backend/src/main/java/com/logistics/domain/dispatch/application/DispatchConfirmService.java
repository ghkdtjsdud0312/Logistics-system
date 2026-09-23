package com.logistics.domain.dispatch.application;

import com.logistics.domain.anomaly.application.AnomalyService;
import com.logistics.domain.anomaly.domain.AnomalyType;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 배차 유스케이스 - 배차 확정 (TASK-006)
 * - 차량/기사 행은 비관적 잠금으로 먼저 확보해 동시 확정 시 하나만 통과하도록 한다.
 * - 출고 계획 중복 배정은 DB unique 제약(dispatch_outbound_link)으로 최종 차단한다.
 * - 거부(적재 초과/일정 충돌/중복 배정)는 Dispatch가 아직 없으므로 출고 계획 조합을 기준으로 이상을 기록한다 (ADR-009).
 */
@Service
@RequiredArgsConstructor
public class DispatchConfirmService {

    private final OutboundService outboundService;
    private final VehicleService vehicleService;
    private final DriverService driverService;
    private final DispatchRepository dispatchRepository;
    private final AnomalyService anomalyService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Dispatch confirm(DispatchConfirmRequest request) {
        String fingerprintKey = request.outboundIds().toString();
        DispatchWindow window = DispatchWindow.of(request.plannedAt());
        validateOutboundsNotAssigned(request.outboundIds(), fingerprintKey);
        OutboundLoadSummary load = outboundService.summarizeLoad(request.outboundIds());

        Vehicle vehicle = vehicleService.getVehicleForUpdate(request.vehicleId());
        validateVehicle(vehicle, load, window, fingerprintKey);

        Driver driver = driverService.getDriverForUpdate(request.driverId());
        validateDriver(driver, window, fingerprintKey);

        Dispatch dispatch = Dispatch.builder()
                .vehicleId(vehicle.getId())
                .driverId(driver.getId())
                .outboundIds(request.outboundIds())
                .plannedAt(request.plannedAt())
                .totalWeightKg(load.totalWeightKg())
                .totalVolumeM3(load.totalVolumeM3())
                .build();

        Dispatch saved = saveOrRejectDuplicate(dispatch, fingerprintKey);
        eventPublisher.publishEvent(new DispatchStatusChangedEvent(saved.getId(), null, saved.getStatus(), saved.getVersion()));
        return saved;
    }

    private void validateOutboundsNotAssigned(List<Long> outboundIds, String fingerprintKey) {
        boolean alreadyAssigned = outboundIds.stream().anyMatch(dispatchRepository::existsByOutboundIdsContaining);
        if (alreadyAssigned) {
            anomalyService.record(AnomalyType.DUPLICATE_ASSIGNMENT, null, fingerprintKey, "이미 배차된 출고 계획이 포함되어 있습니다.");
            throw new BusinessException(ErrorCode.DISPATCH_DUPLICATE_ASSIGNMENT);
        }
    }

    private void validateVehicle(Vehicle vehicle, OutboundLoadSummary load, DispatchWindow window, String fingerprintKey) {
        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            throw new BusinessException(ErrorCode.DISPATCH_VEHICLE_UNAVAILABLE);
        }
        if (!CapacityValidator.fits(vehicle, load.totalWeightKg(), load.totalVolumeM3())) {
            anomalyService.record(AnomalyType.OVER_CAPACITY, null, fingerprintKey,
                    "차량 " + vehicle.getVehicleNumber() + "의 적재 한도를 초과했습니다.");
            throw new BusinessException(ErrorCode.DISPATCH_OVER_CAPACITY);
        }
        List<DispatchWindow> existing = dispatchRepository.findActiveByVehicleId(vehicle.getId()).stream()
                .map(Dispatch::window).toList();
        if (ScheduleConflictChecker.hasConflict(window, existing)) {
            throw new BusinessException(ErrorCode.DISPATCH_VEHICLE_UNAVAILABLE);
        }
    }

    private void validateDriver(Driver driver, DispatchWindow window, String fingerprintKey) {
        if (driver.getStatus() != DriverStatus.AVAILABLE) {
            anomalyService.record(AnomalyType.DRIVER_SCHEDULE_CONFLICT, null, fingerprintKey,
                    "기사 " + driver.getName() + "은(는) 가용 상태가 아닙니다.");
            throw new BusinessException(ErrorCode.DISPATCH_DRIVER_SCHEDULE_CONFLICT);
        }
        List<DispatchWindow> existing = dispatchRepository.findActiveByDriverId(driver.getId()).stream()
                .map(Dispatch::window).toList();
        if (ScheduleConflictChecker.hasConflict(window, existing)) {
            anomalyService.record(AnomalyType.DRIVER_SCHEDULE_CONFLICT, null, fingerprintKey,
                    "기사 " + driver.getName() + "의 일정이 겹칩니다.");
            throw new BusinessException(ErrorCode.DISPATCH_DRIVER_SCHEDULE_CONFLICT);
        }
    }

    private Dispatch saveOrRejectDuplicate(Dispatch dispatch, String fingerprintKey) {
        try {
            return dispatchRepository.save(dispatch);
        } catch (DataIntegrityViolationException e) {
            anomalyService.record(AnomalyType.DUPLICATE_ASSIGNMENT, null, fingerprintKey,
                    "동시 요청으로 이미 다른 배차에 포함된 출고 계획입니다.");
            throw new BusinessException(ErrorCode.DISPATCH_DUPLICATE_ASSIGNMENT);
        }
    }
}
