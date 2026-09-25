package com.logistics.domain.warehouse.infrastructure;

import com.logistics.domain.warehouse.domain.PickingTask;
import com.logistics.domain.warehouse.domain.WorkStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PickingTaskJpaRepository extends JpaRepository<PickingTask, Long> {

    List<PickingTask> findAllByStatusOrderByIdDesc(WorkStatus status);

    List<PickingTask> findAllByOrderByIdDesc();

    long countByOrderIdAndStatusNot(Long orderId, WorkStatus status);
}
