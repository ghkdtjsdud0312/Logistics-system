package com.logistics.domain.inbound.infrastructure;

import com.logistics.domain.inbound.domain.Inbound;
import com.logistics.domain.inbound.domain.InboundRepository;
import com.logistics.domain.inbound.domain.InboundStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InboundRepositoryImpl implements InboundRepository {

    private final InboundJpaRepository jpaRepository;

    @Override
    public Inbound save(Inbound inbound) {
        return jpaRepository.saveAndFlush(inbound);
    }

    @Override
    public Optional<Inbound> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Inbound> findAllByStatus(InboundStatus status) {
        return status == null ? jpaRepository.findAllByOrderByIdDesc()
                : jpaRepository.findAllByStatusOrderByIdDesc(status);
    }
}
