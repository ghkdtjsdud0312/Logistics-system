package com.logistics.domain.outbound.presentation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OutboundCreateRequest(
        @NotBlank(message = "도착지는 필수입니다.") String destination,
        double latitude,
        double longitude,
        @NotEmpty(message = "출고 항목은 1개 이상이어야 합니다.") @Valid List<OutboundItemRequest> items
) {
}
