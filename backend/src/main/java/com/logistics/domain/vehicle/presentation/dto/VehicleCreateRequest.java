package com.logistics.domain.vehicle.presentation.dto;

import com.logistics.domain.vehicle.domain.Vehicle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record VehicleCreateRequest(
        @NotBlank(message = "차량번호는 필수입니다.") String vehicleNumber,
        @NotBlank(message = "차량 유형은 필수입니다.") String vehicleType,
        @PositiveOrZero(message = "최대 중량은 0 이상이어야 합니다.") double maxWeightKg,
        @PositiveOrZero(message = "최대 부피는 0 이상이어야 합니다.") double maxVolumeM3,
        @PositiveOrZero(message = "허브 거리는 0 이상이어야 합니다.") double hubDistanceKm
) {
    public Vehicle toEntity() {
        return Vehicle.builder()
                .vehicleNumber(vehicleNumber)
                .vehicleType(vehicleType)
                .maxWeightKg(maxWeightKg)
                .maxVolumeM3(maxVolumeM3)
                .hubDistanceKm(hubDistanceKm)
                .build();
    }
}
