package com.logistics.domain.audit.infrastructure;

import com.logistics.domain.audit.domain.AuditLog;
import com.logistics.domain.audit.domain.AuditSearchCriteria;
import org.springframework.data.jpa.domain.Specification;

/** 감사로그 검색 조건을 JPA Specification으로 변환한다. */
final class AuditLogSpecifications {

    private AuditLogSpecifications() {
    }

    static Specification<AuditLog> from(AuditSearchCriteria c) {
        return Specification.<AuditLog>where(null)
                .and(c.from() == null ? null : (r, q, cb) -> cb.greaterThanOrEqualTo(r.get("occurredAt"), c.from().atStartOfDay()))
                .and(c.to() == null ? null : (r, q, cb) -> cb.lessThan(r.get("occurredAt"), c.to().plusDays(1).atStartOfDay()))
                .and(blank(c.actor()) ? null : (r, q, cb) -> cb.like(r.get("actor"), "%" + c.actor().trim() + "%"))
                .and(blank(c.target()) ? null : (r, q, cb) -> cb.like(r.get("targetNo"), "%" + c.target().trim() + "%"))
                .and(blank(c.action()) ? null : (r, q, cb) -> cb.equal(r.get("action"), c.action().trim()));
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
