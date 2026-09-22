package com.logistics.domain.outbound.presentation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OutboundItemRequest(
        @NotNull(message = "입고 ID는 필수입니다.") Long inboundId,
        @Positive(message = "수량은 0보다 커야 합니다.") int quantity
) {
}
