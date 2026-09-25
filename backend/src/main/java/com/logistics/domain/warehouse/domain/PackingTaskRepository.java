package com.logistics.domain.warehouse.domain;

import java.util.List;
import java.util.Optional;

public interface PackingTaskRepository {

    PackingTask save(PackingTask task);

    Optional<PackingTask> findById(Long id);

    /** status가 null이면 전체, 최신순 */
    List<PackingTask> findAllByStatus(WorkStatus status);
}
