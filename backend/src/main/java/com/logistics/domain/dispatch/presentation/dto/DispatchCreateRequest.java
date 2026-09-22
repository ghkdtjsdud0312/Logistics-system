package com.logistics.domain.dispatch.presentation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record DispatchCreateRequest(
        @NotBlank(message = "기사명은 필수입니다.") String driverName,
        @NotBlank(message = "차량번호는 필수입니다.") String vehicleNumber,
        @NotEmpty(message = "경유지는 최소 1개 이상이어야 합니다.") @Valid List<WaypointDto> waypoints
) {
}
