package com.logistics.domain.anomaly.presentation.dto;

import com.logistics.domain.anomaly.domain.Anomaly;
import com.logistics.domain.anomaly.domain.AnomalyStatus;
import com.logistics.domain.anomaly.domain.AnomalyType;

import java.time.LocalDateTime;

public record AnomalyResponse(
        Long id,
        AnomalyType type,
        Long dispatchId,
        String message,
        AnomalyStatus status,
        LocalDateTime createdAt
) {
    public static AnomalyResponse from(Anomaly anomaly) {
        return new AnomalyResponse(
                anomaly.getId(),
                anomaly.getType(),
                anomaly.getDispatchId(),
                anomaly.getMessage(),
                anomaly.getStatus(),
                anomaly.getCreatedAt()
        );
    }
}
