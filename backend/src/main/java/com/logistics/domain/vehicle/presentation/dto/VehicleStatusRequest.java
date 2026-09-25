package com.logistics.domain.vehicle.presentation.dto;

import com.logistics.domain.vehicle.domain.VehicleStatus;
import jakarta.validation.constraints.NotNull;

public record VehicleStatusRequest(@NotNull(message = "상태는 필수입니다.") VehicleStatus status) {
}
