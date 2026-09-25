package com.logistics.domain.audit.presentation.dto;

import com.logistics.domain.audit.application.AuditActionLabels;
import com.logistics.domain.audit.domain.AuditLog;

import java.time.LocalDateTime;

/** description은 작업 코드를 한글로 바꾼 표시용 문구다. */
public record AuditLogResponse(
        LocalDateTime occurredAt,
        String actor,
        String targetType,
        String targetNo,
        String action,
        String description,
        String fromStatus,
        String toStatus
) {
    public static AuditLogResponse from(AuditLog log) {
        return new AuditLogResponse(log.getOccurredAt(), log.getActor(), log.getTargetType(), log.getTargetNo(),
                log.getAction(), AuditActionLabels.of(log.getTargetType(), log.getAction()),
                log.getFromStatus(), log.getToStatus());
    }
}
