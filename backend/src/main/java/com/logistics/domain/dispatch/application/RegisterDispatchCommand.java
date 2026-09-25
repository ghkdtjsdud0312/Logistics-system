package com.logistics.domain.dispatch.application;

import java.time.LocalDateTime;
import java.util.List;

/** 배차 등록 입력 */
public record RegisterDispatchCommand(
        Long vehicleId,
        Long driverId,
        LocalDateTime plannedStartAt,
        LocalDateTime plannedArrivalAt,
        List<Long> shipmentIds
) {
}
