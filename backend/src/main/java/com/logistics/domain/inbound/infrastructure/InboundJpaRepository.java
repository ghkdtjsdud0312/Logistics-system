package com.logistics.domain.inbound.infrastructure;

import com.logistics.domain.inbound.domain.Inbound;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA 리포지토리 (기술 구현 상세)
 */
public interface InboundJpaRepository extends JpaRepository<Inbound, Long> {
}
