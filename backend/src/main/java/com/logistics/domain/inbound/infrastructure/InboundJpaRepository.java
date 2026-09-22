package com.logistics.domain.inbound.infrastructure;

import com.logistics.domain.inbound.domain.Inbound;
import com.logistics.domain.inbound.domain.InboundStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA 리포지토리 (기술 구현 상세)
 */
public interface InboundJpaRepository extends JpaRepository<Inbound, Long> {

    List<Inbound> findByStatus(InboundStatus status);
}
