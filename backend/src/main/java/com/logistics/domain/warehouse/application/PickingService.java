package com.logistics.domain.warehouse.application;

import com.logistics.domain.inventory.application.StockReservationService;
import com.logistics.domain.inventory.application.StockService;
import com.logistics.domain.inventory.domain.StockReservation;
import com.logistics.domain.order.application.OrderTransitionService;
import com.logistics.domain.warehouse.domain.PickingTask;
import com.logistics.domain.warehouse.domain.PickingTaskRepository;
import com.logistics.domain.warehouse.domain.WorkStatus;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import com.logistics.global.event.StatusChangedEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** 예약 위치별 피킹 작업 생성·시작·완료. 완료하면 재고가 차감된다. */
@Service
@RequiredArgsConstructor
@Transactional
public class PickingService {

    private final PickingTaskRepository taskRepository;
    private final StockReservationService reservationService;
    private final StockService stockService;
    private final OrderTransitionService orderTransitionService;
    private final PackingService packingService;
    private final StatusChangedEventPublisher eventPublisher;

    public List<PickingTask> createTasks(Long orderId) {
        List<StockReservation> reservations = reservationService.getReservations(orderId);
        return reservations.stream().map(r -> {
            PickingTask task = taskRepository.save(new PickingTask(
                    orderId, r.getOrderItemId(), r.getProductId(), r.getLocationId(), r.getQuantity()));
            task.assignNo();
            return task;
        }).toList();
    }

    public PickingTask start(Long taskId) {
        PickingTask task = get(taskId);
        task.start();
        orderTransitionService.startPicking(task.getOrderId());
        return task;
    }

    public PickingTask complete(Long taskId, int pickedQty) {
        PickingTask task = get(taskId);
        task.complete(pickedQty);
        stockService.consumeReserved(task.getProductId(), task.getLocationId(), pickedQty);
        orderTransitionService.recordPicked(task.getOrderId(), task.getOrderItemId(), pickedQty);
        eventPublisher.publish("PICKING_TASK", task.getId(), task.getTaskNo(), task.getOrderId(),
                "PICK_COMPLETE", WorkStatus.IN_PROGRESS.name(), WorkStatus.COMPLETED.name());
        if (taskRepository.allCompleted(task.getOrderId())) {
            orderTransitionService.completePicking(task.getOrderId());
            packingService.createTask(task.getOrderId());
        }
        return task;
    }

    private PickingTask get(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException(ErrorCode.WORK_TASK_NOT_FOUND));
    }
}
