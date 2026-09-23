package com.logistics.domain.dispatch.presentation.dto;

import java.util.List;

public record DispatchCandidateResponse(
        double totalWeightKg,
        double totalVolumeM3,
        List<VehicleCandidateDto> candidates,
        List<ExcludedVehicleDto> excluded
) {
}
