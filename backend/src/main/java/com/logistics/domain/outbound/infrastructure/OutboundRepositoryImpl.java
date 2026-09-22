package com.logistics.domain.outbound.infrastructure;

import com.logistics.domain.outbound.domain.Outbound;
import com.logistics.domain.outbound.domain.OutboundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OutboundRepositoryImpl implements OutboundRepository {

    private final OutboundJpaRepository outboundJpaRepository;

    @Override
    public Outbound save(Outbound outbound) {
        return outboundJpaRepository.save(outbound);
    }

    @Override
    public Optional<Outbound> findById(Long id) {
        return outboundJpaRepository.findById(id);
    }

    @Override
    public List<Outbound> findAll() {
        return outboundJpaRepository.findAll();
    }
}
