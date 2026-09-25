package com.logistics.domain.order.application;

import com.logistics.domain.order.domain.Order;
import com.logistics.domain.order.domain.OrderStatus;
import com.logistics.global.event.StatusChangedEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 주문 상태를 한 단계 옮기고 이벤트를 발행하는 공통 로직 */
@Component
@RequiredArgsConstructor
class OrderStatusMover {

    private final OrderService orderService;
    private final StatusChangedEventPublisher eventPublisher;

    Order move(Long orderId, OrderStatus to, String action) {
        Order order = orderService.get(orderId);
        OrderStatus from = order.getStatus();
        order.moveTo(to);
        eventPublisher.publish("ORDER", order.getId(), order.getOrderNo(), order.getId(),
                action, from.name(), to.name());
        return order;
    }
}
