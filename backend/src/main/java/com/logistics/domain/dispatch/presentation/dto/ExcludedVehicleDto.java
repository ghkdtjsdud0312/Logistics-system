package com.logistics.domain.dispatch.presentation.dto;

public record ExcludedVehicleDto(
        Long vehicleId,
        String vehicleNumber,
        String reasonCode,
        String reasonMessage
) {
}
