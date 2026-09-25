package com.logistics.domain.inbound.presentation.dto;

import jakarta.validation.constraints.NotNull;

public record InboundPutawayRequest(@NotNull(message = "적치 위치는 필수입니다.") Long locationId) {
}
