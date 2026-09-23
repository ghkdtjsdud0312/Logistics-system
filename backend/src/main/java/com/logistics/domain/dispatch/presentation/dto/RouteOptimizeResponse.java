package com.logistics.domain.dispatch.presentation.dto;

import com.logistics.domain.dispatch.domain.Dispatch;

import java.util.List;

public record RouteOptimizeResponse(
        Long dispatchId,
        String algorithm,
        double initialDistanceKm,
        double optimizedDistanceKm,
        double improvementRate,
        List<RouteStopDto> stops
) {
    public static RouteOptimizeResponse from(Dispatch dispatch) {
        return new RouteOptimizeResponse(
                dispatch.getId(),
                dispatch.getRouteAlgorithm(),
                dispatch.getInitialDistanceKm(),
                dispatch.getOptimizedDistanceKm(),
                dispatch.getImprovementRate(),
                dispatch.getStops().stream().map(RouteStopDto::from).toList()
        );
    }
}
