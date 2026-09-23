package com.logistics.domain.outbound.presentation.dto;

import com.logistics.domain.outbound.domain.Outbound;
import com.logistics.domain.outbound.domain.OutboundStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OutboundResponse(
        Long id,
        String destination,
        double latitude,
        double longitude,
        OutboundStatus status,
        List<OutboundItemResponse> items,
        int totalQuantity,
        double totalWeightKg,
        double totalVolumeM3,
        LocalDateTime createdAt
) {
    public static OutboundResponse from(Outbound outbound) {
        return new OutboundResponse(
                outbound.getId(),
                outbound.getDestination(),
                outbound.getLatitude(),
                outbound.getLongitude(),
                outbound.getStatus(),
                outbound.getItems().stream().map(OutboundItemResponse::from).toList(),
                outbound.totalQuantity(),
                outbound.totalWeightKg(),
                outbound.totalVolumeM3(),
                outbound.getCreatedAt()
        );
    }
}
