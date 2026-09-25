package com.logistics.domain.delivery.presentation.dto;

import jakarta.validation.constraints.Positive;

public record DeliverRequest(@Positive(message = "인도수량은 0보다 커야 합니다.") int deliveredQty) {
}
