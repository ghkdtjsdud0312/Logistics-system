package com.logistics.domain.audit.domain;

import java.util.List;

public interface AuditLogRepository {

    AuditLog save(AuditLog log);

    /** 최신순 */
    List<AuditLog> search(AuditSearchCriteria criteria);

    /** 주문에 관련된 로그, 오래된 순 */
    List<AuditLog> findByOrderId(Long orderId);
}
