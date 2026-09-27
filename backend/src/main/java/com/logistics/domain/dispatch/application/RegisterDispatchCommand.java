package com.logistics.domain.dispatch.application;

import java.time.LocalDateTime;
import java.util.List;

/** 배차 생성 입력. shipmentIds는 없어도 된다. */
public record RegisterDispatchCommand(
        Long vehicleId,
        Long driverId,
        Long warehouseId,
        LocalDateTime plannedStartAt,
        LocalDateTime plannedArrivalAt,
        List<Long> shipmentIds
) {

    /** 출발지 없이 만드는 경우 */
    public RegisterDispatchCommand(Long vehicleId, Long driverId, LocalDateTime plannedStartAt,
                                   LocalDateTime plannedArrivalAt, List<Long> shipmentIds) {
        this(vehicleId, driverId, null, plannedStartAt, plannedArrivalAt, shipmentIds);
    }
}
