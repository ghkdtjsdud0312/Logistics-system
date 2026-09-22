package com.logistics.domain.outbound.infrastructure;

import com.logistics.domain.outbound.domain.Outbound;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboundJpaRepository extends JpaRepository<Outbound, Long> {
}
