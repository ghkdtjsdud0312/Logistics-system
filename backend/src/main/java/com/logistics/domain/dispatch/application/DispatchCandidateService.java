package com.logistics.domain.dispatch.application;

import com.logistics.domain.dispatch.domain.*;
import com.logistics.domain.dispatch.presentation.dto.DispatchCandidateRequest;
import com.logistics.domain.dispatch.presentation.dto.DispatchCandidateResponse;
import com.logistics.domain.dispatch.presentation.dto.ExcludedVehicleDto;
import com.logistics.domain.dispatch.presentation.dto.VehicleCandidateDto;
import com.logistics.domain.outbound.application.OutboundLoadSummary;
import com.logistics.domain.outbound.application.OutboundService;
import com.logistics.domain.vehicle.domain.CapacityValidator;
import com.logistics.domain.vehicle.domain.Vehicle;
import com.logistics.domain.vehicle.domain.VehicleStatus;
import com.logistics.domain.vehicle.application.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 배차 유스케이스 - 차량 후보 조회 (TASK-005)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DispatchCandidateService {

    private final OutboundService outboundService;
    private final VehicleService vehicleService;
    private final DispatchRepository dispatchRepository;

    public DispatchCandidateResponse findCandidates(DispatchCandidateRequest request) {
        OutboundLoadSummary load = outboundService.summarizeLoad(request.outboundIds());
        DispatchWindow window = DispatchWindow.of(request.plannedAt());

        List<VehicleCandidateDto> candidates = new ArrayList<>();
        List<ExcludedVehicleDto> excluded = new ArrayList<>();

        for (Vehicle vehicle : vehicleService.getVehicles(null)) {
            evaluate(vehicle, load, window, candidates, excluded);
        }

        candidates.sort(Comparator.comparingDouble(VehicleCandidateDto::score));
        return new DispatchCandidateResponse(load.totalWeightKg(), load.totalVolumeM3(), candidates, excluded);
    }

    private void evaluate(Vehicle vehicle, OutboundLoadSummary load, DispatchWindow window,
                           List<VehicleCandidateDto> candidates, List<ExcludedVehicleDto> excluded) {
        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            excluded.add(exclude(vehicle, "VEHICLE_UNAVAILABLE", "차량 상태가 가용하지 않습니다 (" + vehicle.getStatus() + ")."));
            return;
        }
        if (!CapacityValidator.fits(vehicle, load.totalWeightKg(), load.totalVolumeM3())) {
            excluded.add(exclude(vehicle, "OVER_CAPACITY", "중량 또는 부피 한도를 초과합니다."));
            return;
        }
        List<DispatchWindow> existing = dispatchRepository.findActiveByVehicleId(vehicle.getId()).stream()
                .map(Dispatch::window).toList();
        if (ScheduleConflictChecker.hasConflict(window, existing)) {
            excluded.add(exclude(vehicle, "SCHEDULE_CONFLICT", "해당 시간에 이미 다른 배차가 있습니다."));
            return;
        }

        double weightRatio = CapacityValidator.weightRatio(vehicle, load.totalWeightKg());
        double volumeRatio = CapacityValidator.volumeRatio(vehicle, load.totalVolumeM3());
        double score = CandidateScorer.score(weightRatio, volumeRatio, vehicle.getHubDistanceKm());
        candidates.add(new VehicleCandidateDto(vehicle.getId(), vehicle.getVehicleNumber(),
                weightRatio, volumeRatio, score, "잔여 적재율과 허브 거리 기준 추천"));
    }

    private ExcludedVehicleDto exclude(Vehicle vehicle, String code, String message) {
        return new ExcludedVehicleDto(vehicle.getId(), vehicle.getVehicleNumber(), code, message);
    }
}
