package com.logistics.domain.vehicle.presentation.dto;

import com.logistics.domain.vehicle.domain.Vehicle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record VehicleCreateRequest(
        @NotBlank(message = "차량번호는 필수입니다.") String vehicleNumber,
        @NotBlank(message = "차량 종류는 필수입니다.") String vehicleType,
        @Positive(message = "적재량은 0보다 커야 합니다.") double capacityKg
) {
    public Vehicle toEntity() {
        return Vehicle.builder()
                .vehicleNumber(vehicleNumber)
                .vehicleType(vehicleType)
                .capacityKg(capacityKg)
                .build();
    }
}
