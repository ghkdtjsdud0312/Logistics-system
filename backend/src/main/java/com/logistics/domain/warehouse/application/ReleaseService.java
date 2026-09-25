package com.logistics.domain.warehouse.application;

import com.logistics.domain.order.application.OrderTransitionService;
import com.logistics.domain.order.domain.Order;
import com.logistics.domain.warehouse.domain.PickingTask;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** 출고 지시: 주문을 출고대기로 바꾸고 예약 위치별 피킹 작업을 만든다. */
@Service
@RequiredArgsConstructor
@Transactional
public class ReleaseService {

    private final OrderTransitionService orderTransitionService;
    private final PickingService pickingService;

    public Released release(Long orderId) {
        Order order = orderTransitionService.release(orderId);
        List<PickingTask> tasks = pickingService.createTasks(orderId);
        return new Released(order.getOrderNo(), order.getStatus().name(), tasks.size());
    }

    public record Released(String orderNo, String status, int pickingTaskCount) {
    }
}
