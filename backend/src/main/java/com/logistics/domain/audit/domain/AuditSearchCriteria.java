package com.logistics.domain.audit.domain;

import java.time.LocalDate;

/** 감사로그 검색 조건. null이거나 빈 조건은 무시한다. */
public record AuditSearchCriteria(LocalDate from, LocalDate to, String actor, String target, String action, int limit) {
}
