package com.logistics.domain.dispatch.presentation.dto;

public record VehicleCandidateDto(
        Long vehicleId,
        String vehicleNumber,
        double usedWeightRatio,
        double usedVolumeRatio,
        double score,
        String reason
) {
}
