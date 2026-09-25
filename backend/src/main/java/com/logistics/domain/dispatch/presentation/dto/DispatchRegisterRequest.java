package com.logistics.domain.dispatch.presentation.dto;

import com.logistics.domain.dispatch.application.RegisterDispatchCommand;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record DispatchRegisterRequest(
        @NotNull(message = "차량은 필수입니다.") Long vehicleId,
        @NotNull(message = "기사는 필수입니다.") Long driverId,
        @NotNull(message = "출발 예정 시각은 필수입니다.") LocalDateTime plannedStartAt,
        @NotNull(message = "배송 예정 시각은 필수입니다.") LocalDateTime plannedArrivalAt,
        @NotEmpty(message = "배차할 배송을 선택하세요.") List<Long> shipmentIds
) {
    public RegisterDispatchCommand toCommand() {
        return new RegisterDispatchCommand(vehicleId, driverId, plannedStartAt, plannedArrivalAt, shipmentIds);
    }
}
