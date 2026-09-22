package com.logistics.domain.inbound.presentation.dto;

import jakarta.validation.constraints.PositiveOrZero;

public record InboundCompleteRequest(
        @PositiveOrZero(message = "검수 수량은 0 이상이어야 합니다.") int inspectedQuantity
) {
}
