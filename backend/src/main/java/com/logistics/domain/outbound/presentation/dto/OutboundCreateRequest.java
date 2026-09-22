package com.logistics.domain.outbound.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record OutboundCreateRequest(
        @NotBlank(message = "품목명은 필수입니다.") String itemName,
        @Positive(message = "수량은 0보다 커야 합니다.") int quantity,
        @NotBlank(message = "도착지는 필수입니다.") String destination
) {
}
