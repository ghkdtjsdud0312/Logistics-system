package com.logistics.domain.inbound.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record InboundCreateRequest(
        @NotBlank(message = "품목명은 필수입니다.") String itemName,
        @Positive(message = "수량은 0보다 커야 합니다.") int quantity,
        @NotBlank(message = "창고 위치는 필수입니다.") String warehouseLocation
) {
}
