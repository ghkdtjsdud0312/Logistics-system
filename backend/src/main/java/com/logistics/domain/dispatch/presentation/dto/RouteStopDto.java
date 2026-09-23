package com.logistics.domain.dispatch.presentation.dto;

import com.logistics.domain.dispatch.domain.RouteStop;
import com.logistics.domain.dispatch.domain.RouteStopStatus;

public record RouteStopDto(
        Long id,
        int sequence,
        Long outboundId,
        String label,
        double latitude,
        double longitude,
        double distanceFromPreviousKm,
        RouteStopStatus status
) {
    public static RouteStopDto from(RouteStop stop) {
        return new RouteStopDto(
                stop.getId(),
                stop.getSequence(),
                stop.getOutboundId(),
                stop.getLabel(),
                stop.getLatitude(),
                stop.getLongitude(),
                stop.getDistanceFromPreviousKm(),
                stop.getStatus()
        );
    }
}
