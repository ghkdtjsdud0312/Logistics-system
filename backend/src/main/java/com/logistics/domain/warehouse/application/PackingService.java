package com.logistics.domain.warehouse.application;

import com.logistics.domain.order.application.OrderTransitionService;
import com.logistics.domain.warehouse.domain.PackingTask;
import com.logistics.domain.warehouse.domain.PackingTaskRepository;
import com.logistics.domain.warehouse.domain.WorkStatus;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import com.logistics.global.event.StatusChangedEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 포장 작업 생성과 완료. 완료하면 주문이 포장완료가 된다. */
@Service
@RequiredArgsConstructor
@Transactional
public class PackingService {

    private final PackingTaskRepository taskRepository;
    private final OrderTransitionService orderTransitionService;
    private final StatusChangedEventPublisher eventPublisher;

    public PackingTask createTask(Long orderId) {
        PackingTask task = taskRepository.save(new PackingTask(orderId));
        task.assignNo();
        return task;
    }

    public PackingTask complete(Long taskId, String boxCode) {
        PackingTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException(ErrorCode.WORK_TASK_NOT_FOUND));
        task.complete(boxCode);
        orderTransitionService.completePacking(task.getOrderId());
        eventPublisher.publish("PACKING_TASK", task.getId(), task.getTaskNo(), task.getOrderId(),
                "PACK_COMPLETE", WorkStatus.WAITING.name(), WorkStatus.COMPLETED.name());
        return task;
    }
}
