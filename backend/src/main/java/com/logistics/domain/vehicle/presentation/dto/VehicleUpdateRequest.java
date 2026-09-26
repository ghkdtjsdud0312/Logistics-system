package com.logistics.domain.vehicle.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record VehicleUpdateRequest(
        @NotBlank(message = "차량 종류는 필수입니다.") String vehicleType,
        @Positive(message = "적재량은 0보다 커야 합니다.") double capacityKg
) {
}
