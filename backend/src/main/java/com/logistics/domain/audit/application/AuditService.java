package com.logistics.domain.audit.application;

import com.logistics.domain.audit.domain.AuditLog;
import com.logistics.domain.audit.domain.AuditLogRepository;
import com.logistics.domain.audit.domain.ProcessedEventRepository;
import com.logistics.global.event.StatusChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;

/** 상태 변경 이벤트를 감사로그로 기록한다. 같은 이벤트를 다시 받아도 한 번만 기록한다. */
@Service
@RequiredArgsConstructor
@Transactional
public class AuditService {

    static final String CONSUMER = "audit";

    private final AuditLogRepository auditLogRepository;
    private final ProcessedEventRepository processedEventRepository;

    /** 새로 기록했으면 true, 중복 이벤트라 건너뛰었으면 false */
    public boolean record(StatusChangedEvent event) {
        if (!processedEventRepository.markProcessed(CONSUMER, event.eventId())) {
            return false;
        }
        auditLogRepository.save(AuditLog.from(event, ZoneId.systemDefault()));
        return true;
    }
}
