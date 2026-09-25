package com.logistics.domain.warehouse.domain;

import java.util.List;
import java.util.Optional;

public interface PickingTaskRepository {

    PickingTask save(PickingTask task);

    Optional<PickingTask> findById(Long id);

    /** status가 null이면 전체, 최신순 */
    List<PickingTask> findAllByStatus(WorkStatus status);

    /** 주문의 모든 피킹 작업이 완료되었는지 */
    boolean allCompleted(Long orderId);
}
