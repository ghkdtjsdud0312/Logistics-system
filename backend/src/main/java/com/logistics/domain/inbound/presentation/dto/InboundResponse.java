package com.logistics.domain.inbound.presentation.dto;

import com.logistics.domain.inbound.domain.Inbound;
import com.logistics.domain.inbound.domain.InboundStatus;

import java.time.LocalDateTime;

public record InboundResponse(
        Long id,
        String itemName,
        int quantity,
        String warehouseLocation,
        InboundStatus status,
        LocalDateTime createdAt
) {
    public static InboundResponse from(Inbound inbound) {
        return new InboundResponse(
                inbound.getId(),
                inbound.getItemName(),
                inbound.getQuantity(),
                inbound.getWarehouseLocation(),
                inbound.getStatus(),
                inbound.getCreatedAt()
        );
    }
}
