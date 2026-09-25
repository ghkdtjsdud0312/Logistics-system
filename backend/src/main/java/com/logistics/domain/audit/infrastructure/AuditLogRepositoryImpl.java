package com.logistics.domain.audit.infrastructure;

import com.logistics.domain.audit.domain.AuditLog;
import com.logistics.domain.audit.domain.AuditLogRepository;
import com.logistics.domain.audit.domain.AuditSearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AuditLogRepositoryImpl implements AuditLogRepository {

    private final AuditLogJpaRepository jpaRepository;

    @Override
    public AuditLog save(AuditLog log) {
        return jpaRepository.save(log);
    }

    @Override
    public List<AuditLog> search(AuditSearchCriteria criteria) {
        PageRequest page = PageRequest.of(0, criteria.limit(),
                Sort.by(Sort.Direction.DESC, "occurredAt").and(Sort.by(Sort.Direction.DESC, "id")));
        return jpaRepository.findAll(AuditLogSpecifications.from(criteria), page).getContent();
    }

    @Override
    public List<AuditLog> findByOrderId(Long orderId) {
        return jpaRepository.findAllByOrderIdOrderByOccurredAtAscIdAsc(orderId);
    }
}
