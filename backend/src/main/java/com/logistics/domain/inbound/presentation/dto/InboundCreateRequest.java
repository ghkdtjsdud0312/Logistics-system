package com.logistics.domain.inbound.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record InboundCreateRequest(
        @NotBlank(message = "거래처는 필수입니다.") String partnerName,
        @NotNull(message = "상품은 필수입니다.") Long productId,
        @Positive(message = "수량은 0보다 커야 합니다.") int quantity,
        @NotNull(message = "입고일은 필수입니다.") LocalDate inboundDate
) {
}
