package com.logistics.domain.vehicle.presentation.dto;

import com.logistics.domain.vehicle.domain.Vehicle;
import com.logistics.domain.vehicle.domain.VehicleStatus;

public record VehicleResponse(
        Long id,
        String vehicleNumber,
        String vehicleType,
        double maxWeightKg,
        double maxVolumeM3,
        VehicleStatus status
) {
    public static VehicleResponse from(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getVehicleNumber(),
                vehicle.getVehicleType(),
                vehicle.getMaxWeightKg(),
                vehicle.getMaxVolumeM3(),
                vehicle.getStatus()
        );
    }
}
