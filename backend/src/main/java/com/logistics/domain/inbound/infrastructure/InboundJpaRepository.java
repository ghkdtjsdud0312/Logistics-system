package com.logistics.domain.inbound.infrastructure;

import com.logistics.domain.inbound.domain.Inbound;
import com.logistics.domain.inbound.domain.InboundStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InboundJpaRepository extends JpaRepository<Inbound, Long> {

    List<Inbound> findAllByStatusOrderByIdDesc(InboundStatus status);

    List<Inbound> findAllByOrderByIdDesc();
}
