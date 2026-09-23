package com.logistics.domain.outbound.presentation.dto;

import com.logistics.domain.outbound.domain.OutboundItem;

public record OutboundItemResponse(
        Long inboundId,
        int quantity,
        double weightKg,
        double volumeM3
) {
    public static OutboundItemResponse from(OutboundItem item) {
        return new OutboundItemResponse(item.getInboundId(), item.getQuantity(), item.getWeightKg(), item.getVolumeM3());
    }
}
