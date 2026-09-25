package com.logistics.domain.audit.application;

import com.logistics.domain.audit.domain.AuditLogRepository;
import com.logistics.domain.audit.domain.AuditSearchCriteria;
import com.logistics.domain.audit.presentation.dto.AuditLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** 감사로그 검색과 최근 이벤트 조회 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuditQueryService {

    private static final int MAX_LIMIT = 200;

    private final AuditLogRepository auditLogRepository;

    public List<AuditLogResponse> search(AuditSearchCriteria criteria) {
        return auditLogRepository.search(criteria).stream().map(AuditLogResponse::from).toList();
    }

    public List<AuditLogResponse> recent(int limit) {
        AuditSearchCriteria criteria = new AuditSearchCriteria(null, null, null, null, null, Math.min(limit, MAX_LIMIT));
        return search(criteria);
    }
}
