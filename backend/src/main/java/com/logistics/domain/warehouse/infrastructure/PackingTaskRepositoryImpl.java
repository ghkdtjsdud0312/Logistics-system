package com.logistics.domain.warehouse.infrastructure;

import com.logistics.domain.warehouse.domain.PackingTask;
import com.logistics.domain.warehouse.domain.PackingTaskRepository;
import com.logistics.domain.warehouse.domain.WorkStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PackingTaskRepositoryImpl implements PackingTaskRepository {

    private final PackingTaskJpaRepository jpaRepository;

    @Override
    public PackingTask save(PackingTask task) {
        return jpaRepository.saveAndFlush(task);
    }

    @Override
    public Optional<PackingTask> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<PackingTask> findAllByStatus(WorkStatus status) {
        return status == null ? jpaRepository.findAllByOrderByIdDesc()
                : jpaRepository.findAllByStatusOrderByIdDesc(status);
    }
}
