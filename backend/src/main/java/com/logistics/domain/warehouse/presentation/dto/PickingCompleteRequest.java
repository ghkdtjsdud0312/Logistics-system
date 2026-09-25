package com.logistics.domain.warehouse.presentation.dto;

import jakarta.validation.constraints.Positive;

public record PickingCompleteRequest(@Positive(message = "피킹수량은 0보다 커야 합니다.") int pickedQty) {
}
