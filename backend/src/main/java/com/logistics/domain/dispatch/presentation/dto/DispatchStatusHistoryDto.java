package com.logistics.domain.dispatch.presentation.dto;

import com.logistics.domain.dispatch.domain.DispatchStatus;
import com.logistics.domain.dispatch.domain.DispatchStatusHistory;

import java.time.LocalDateTime;

public record DispatchStatusHistoryDto(
        DispatchStatus fromStatus,
        DispatchStatus toStatus,
        String actor,
        String description,
        LocalDateTime changedAt
) {
    public static DispatchStatusHistoryDto from(DispatchStatusHistory history) {
        return new DispatchStatusHistoryDto(
                history.getFromStatus(),
                history.getToStatus(),
                history.getActor(),
                history.getDescription(),
                history.getCreatedAt()
        );
    }
}
