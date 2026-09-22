package com.logistics.domain.inbound.infrastructure;

import com.logistics.domain.inbound.domain.Inbound;
import com.logistics.domain.inbound.domain.InboundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * InboundRepository 포트의 JPA 구현체 (Adapter)
 */
@Repository
@RequiredArgsConstructor
public class InboundRepositoryImpl implements InboundRepository {

    private final InboundJpaRepository inboundJpaRepository;

    @Override
    public Inbound save(Inbound inbound) {
        return inboundJpaRepository.save(inbound);
    }

    @Override
    public Optional<Inbound> findById(Long id) {
        return inboundJpaRepository.findById(id);
    }

    @Override
    public List<Inbound> findAll() {
        return inboundJpaRepository.findAll();
    }
}
