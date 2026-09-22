package com.logistics.domain.dispatch.presentation.dto;

import com.logistics.domain.dispatch.domain.Waypoint;
import jakarta.validation.constraints.NotBlank;

public record WaypointDto(
        @NotBlank(message = "경유지명은 필수입니다.") String label,
        double latitude,
        double longitude
) {
    public Waypoint toEntity() {
        return new Waypoint(label, latitude, longitude);
    }

    public static WaypointDto from(Waypoint waypoint) {
        return new WaypointDto(waypoint.getLabel(), waypoint.getLatitude(), waypoint.getLongitude());
    }
}
