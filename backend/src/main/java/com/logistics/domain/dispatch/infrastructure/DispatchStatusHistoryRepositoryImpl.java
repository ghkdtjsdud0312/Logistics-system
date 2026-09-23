package com.logistics.domain.dispatch.infrastructure;

import com.logistics.domain.dispatch.domain.DispatchStatusHistory;
import com.logistics.domain.dispatch.domain.DispatchStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DispatchStatusHistoryRepositoryImpl implements DispatchStatusHistoryRepository {

    private final DispatchStatusHistoryJpaRepository jpaRepository;

    @Override
    public DispatchStatusHistory save(DispatchStatusHistory history) {
        return jpaRepository.save(history);
    }

    @Override
    public List<DispatchStatusHistory> findAllByDispatchId(Long dispatchId) {
        return jpaRepository.findAllByDispatchIdOrderByCreatedAtAsc(dispatchId);
    }
}
