package com.logistics.domain.outbound.presentation.dto;

import com.logistics.domain.outbound.domain.OutboundItem;

public record OutboundItemResponse(
        Long inboundId,
        int quantity
) {
    public static OutboundItemResponse from(OutboundItem item) {
        return new OutboundItemResponse(item.getInboundId(), item.getQuantity());
    }
}
