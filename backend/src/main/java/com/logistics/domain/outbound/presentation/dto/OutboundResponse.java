package com.logistics.domain.outbound.presentation.dto;

import com.logistics.domain.outbound.domain.Outbound;
import com.logistics.domain.outbound.domain.OutboundStatus;

import java.time.LocalDateTime;

public record OutboundResponse(
        Long id,
        String itemName,
        int quantity,
        String destination,
        OutboundStatus status,
        LocalDateTime createdAt
) {
    public static OutboundResponse from(Outbound outbound) {
        return new OutboundResponse(
                outbound.getId(),
                outbound.getItemName(),
                outbound.getQuantity(),
                outbound.getDestination(),
                outbound.getStatus(),
                outbound.getCreatedAt()
        );
    }
}
