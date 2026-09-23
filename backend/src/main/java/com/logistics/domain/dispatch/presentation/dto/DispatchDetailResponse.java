package com.logistics.domain.dispatch.presentation.dto;

import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.dispatch.domain.DispatchStatus;
import com.logistics.domain.dispatch.domain.DispatchStatusHistory;

import java.time.LocalDateTime;
import java.util.List;

public record DispatchDetailResponse(
        Long id,
        Long vehicleId,
        Long driverId,
        List<Long> outboundIds,
        LocalDateTime plannedAt,
        double totalWeightKg,
        double totalVolumeM3,
        DispatchStatus status,
        Long version,
        List<RouteStopDto> stops,
        List<DispatchStatusHistoryDto> statusHistory
) {
    public static DispatchDetailResponse of(Dispatch dispatch, List<DispatchStatusHistory> history) {
        return new DispatchDetailResponse(
                dispatch.getId(),
                dispatch.getVehicleId(),
                dispatch.getDriverId(),
                dispatch.getOutboundIds(),
                dispatch.getPlannedAt(),
                dispatch.getTotalWeightKg(),
                dispatch.getTotalVolumeM3(),
                dispatch.getStatus(),
                dispatch.getVersion(),
                dispatch.getStops().stream().map(RouteStopDto::from).toList(),
                history.stream().map(DispatchStatusHistoryDto::from).toList()
        );
    }
}
