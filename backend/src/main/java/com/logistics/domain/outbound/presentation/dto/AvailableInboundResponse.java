package com.logistics.domain.outbound.presentation.dto;

import com.logistics.domain.inbound.domain.Inbound;

public record AvailableInboundResponse(
        Long inboundId,
        String itemName,
        String warehouseLocation,
        int inspectedQuantity,
        int availableQuantity
) {
    public static AvailableInboundResponse of(Inbound inbound, int availableQuantity) {
        return new AvailableInboundResponse(
                inbound.getId(),
                inbound.getItemName(),
                inbound.getWarehouseLocation(),
                inbound.getInspectedQuantity(),
                availableQuantity
        );
    }
}
