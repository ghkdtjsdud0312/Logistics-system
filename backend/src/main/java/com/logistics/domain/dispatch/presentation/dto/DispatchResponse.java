package com.logistics.domain.dispatch.presentation.dto;

import com.logistics.domain.dispatch.domain.Dispatch;
import com.logistics.domain.dispatch.domain.DispatchStatus;
import com.logistics.domain.driver.domain.Driver;
import com.logistics.domain.vehicle.domain.Vehicle;

import java.time.LocalDateTime;
import java.util.List;

public record DispatchResponse(
        Long id,
        String dispatchNo,
        DispatchStatus status,
        String vehicleNumber,
        String driverName,
        LocalDateTime plannedStartAt,
        LocalDateTime plannedArrivalAt,
        LocalDateTime startedAt,
        double totalWeightKg,
        int shipmentCount,
        List<Long> shipmentIds
) {
    public static DispatchResponse of(Dispatch d, Vehicle vehicle, Driver driver, List<Long> shipmentIds) {
        return new DispatchResponse(d.getId(), d.getDispatchNo(), d.getStatus(), vehicle.getVehicleNumber(),
                driver.getName(), d.getPlannedStartAt(), d.getPlannedArrivalAt(), d.getStartedAt(),
                d.getTotalWeightKg(), shipmentIds.size(), shipmentIds);
    }
}
