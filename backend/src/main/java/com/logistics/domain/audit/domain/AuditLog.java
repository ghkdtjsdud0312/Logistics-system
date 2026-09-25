package com.logistics.domain.audit.domain;

import com.logistics.global.event.StatusChangedEvent;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

/** 상태 변경 이력. 한 번 기록하면 수정하지 않는다(append-only). */
@Getter
@Entity
@Table(name = "audit_log", indexes = {
        @Index(columnList = "orderId, occurredAt"),
        @Index(columnList = "occurredAt")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime occurredAt;
    private String actor;
    private String targetType;
    private Long targetId;
    private String targetNo;
    private Long orderId;
    private String action;
    private String fromStatus;
    private String toStatus;

    public static AuditLog from(StatusChangedEvent event, ZoneId zone) {
        AuditLog log = new AuditLog();
        log.occurredAt = LocalDateTime.ofInstant(event.occurredAt(), zone);
        log.actor = event.actor();
        log.targetType = event.targetType();
        log.targetId = event.targetId();
        log.targetNo = event.targetNo();
        log.orderId = event.orderId();
        log.action = event.action();
        log.fromStatus = event.fromStatus();
        log.toStatus = event.toStatus();
        return log;
    }
}
