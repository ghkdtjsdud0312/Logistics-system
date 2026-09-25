package com.logistics.domain.order.application;

import com.logistics.domain.order.domain.Order;
import com.logistics.domain.order.domain.OrderItem;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.logistics.domain.order.domain.OrderStatus.*;

/** 창고 작업(출고 지시, 피킹, 포장)이 호출하는 주문 상태 전이 */
@Service
@RequiredArgsConstructor
@Transactional
public class OrderTransitionService {

    private final OrderService orderService;
    private final OrderStatusMover mover;

    public Order release(Long orderId) {
        return mover.move(orderId, OUTBOUND_WAITING, "RELEASE");
    }

    /** 첫 피킹 작업이 시작될 때만 피킹중으로 바꾼다. 이미 피킹중이면 그대로 둔다. */
    public Order startPicking(Long orderId) {
        Order order = orderService.get(orderId);
        return order.getStatus() == PICKING ? order : mover.move(orderId, PICKING, "PICK_START");
    }

    public Order completePicking(Long orderId) {
        return mover.move(orderId, PICKED, "PICK_COMPLETE");
    }

    public Order completePacking(Long orderId) {
        return mover.move(orderId, PACKED, "PACK_COMPLETE");
    }

    public void recordPicked(Long orderId, Long orderItemId, int quantity) {
        Order order = orderService.get(orderId);
        OrderItem item = order.getItems().stream().filter(i -> i.getId().equals(orderItemId)).findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        item.recordPicked(quantity);
    }
}
