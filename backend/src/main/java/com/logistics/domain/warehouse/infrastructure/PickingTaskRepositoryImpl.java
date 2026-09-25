package com.logistics.domain.warehouse.infrastructure;

import com.logistics.domain.warehouse.domain.PickingTask;
import com.logistics.domain.warehouse.domain.PickingTaskRepository;
import com.logistics.domain.warehouse.domain.WorkStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PickingTaskRepositoryImpl implements PickingTaskRepository {

    private final PickingTaskJpaRepository jpaRepository;

    @Override
    public PickingTask save(PickingTask task) {
        return jpaRepository.saveAndFlush(task);
    }

    @Override
    public Optional<PickingTask> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<PickingTask> findAllByStatus(WorkStatus status) {
        return status == null ? jpaRepository.findAllByOrderByIdDesc()
                : jpaRepository.findAllByStatusOrderByIdDesc(status);
    }

    @Override
    public boolean allCompleted(Long orderId) {
        return jpaRepository.countByOrderIdAndStatusNot(orderId, WorkStatus.COMPLETED) == 0;
    }
}
