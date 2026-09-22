package com.logistics.domain.dispatch.presentation.dto;

import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.dispatch.domain.DispatchStatus;

import java.util.List;

public record DispatchResponse(
        Long id,
        String driverName,
        String vehicleNumber,
        DispatchStatus status,
        List<WaypointDto> waypoints,
        List<WaypointDto> optimizedRoute
) {
    public static DispatchResponse from(Dispatch dispatch) {
        return new DispatchResponse(
                dispatch.getId(),
                dispatch.getDriverName(),
                dispatch.getVehicleNumber(),
                dispatch.getStatus(),
                dispatch.getWaypoints().stream().map(WaypointDto::from).toList(),
                dispatch.getOptimizedRoute().stream().map(WaypointDto::from).toList()
        );
    }
}
