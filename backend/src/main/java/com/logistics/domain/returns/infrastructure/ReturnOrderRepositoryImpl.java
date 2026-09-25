package com.logistics.domain.returns.infrastructure;

import com.logistics.domain.returns.domain.ReturnOrder;
import com.logistics.domain.returns.domain.ReturnOrderRepository;
import com.logistics.domain.returns.domain.ReturnStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReturnOrderRepositoryImpl implements ReturnOrderRepository {

    private final ReturnOrderJpaRepository jpaRepository;

    @Override
    public ReturnOrder save(ReturnOrder returnOrder) {
        return jpaRepository.saveAndFlush(returnOrder);
    }

    @Override
    public Optional<ReturnOrder> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<ReturnOrder> findAllByStatus(ReturnStatus status) {
        return status == null ? jpaRepository.findAllByOrderByIdDesc()
                : jpaRepository.findAllByStatusOrderByIdDesc(status);
    }
}
