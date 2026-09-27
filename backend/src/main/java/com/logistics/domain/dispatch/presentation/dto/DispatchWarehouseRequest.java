package com.logistics.domain.dispatch.presentation.dto;

import jakarta.validation.constraints.NotNull;

public record DispatchWarehouseRequest(@NotNull(message = "출발지 창고는 필수입니다.") Long warehouseId) {
}
