package com.logistics.domain.dispatch.presentation.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record DispatchShipmentsRequest(
        @NotEmpty(message = "배차할 배송을 선택하세요.") List<Long> shipmentIds
) {
}
