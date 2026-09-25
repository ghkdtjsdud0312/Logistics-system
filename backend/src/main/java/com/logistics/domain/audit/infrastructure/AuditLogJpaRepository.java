package com.logistics.domain.audit.infrastructure;

import com.logistics.domain.audit.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AuditLogJpaRepository extends JpaRepository<AuditLog, Long>, JpaSpecificationExecutor<AuditLog> {

    List<AuditLog> findAllByOrderIdOrderByOccurredAtAscIdAsc(Long orderId);
}
