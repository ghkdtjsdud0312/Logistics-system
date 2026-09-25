package com.logistics.domain.warehouse.infrastructure;

import com.logistics.domain.warehouse.domain.PackingTask;
import com.logistics.domain.warehouse.domain.WorkStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PackingTaskJpaRepository extends JpaRepository<PackingTask, Long> {

    List<PackingTask> findAllByStatusOrderByIdDesc(WorkStatus status);

    List<PackingTask> findAllByOrderByIdDesc();
}
