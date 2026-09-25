package com.logistics.domain.order.application;

import com.logistics.domain.order.domain.Order;
import com.logistics.domain.order.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.logistics.domain.order.domain.OrderStatus.*;

/** 상차·배차·배송이 호출하는 주문 상태 전이와 수량 기록 */
@Service
@RequiredArgsConstructor
@Transactional
public class OrderShippingService {

    private final OrderService orderService;
    private final OrderStatusMover mover;

    /** 상차 완료: 모든 품목의 상차수량을 주문수량으로 기록한다. */
    public Order completeLoading(Long orderId) {
        Order order = mover.move(orderId, LOADED, "LOAD_COMPLETE");
        order.getItems().forEach(i -> i.recordLoaded(i.getQuantity()));
        return order;
    }

    public Order markDispatched(Long orderId) {
        return mover.move(orderId, DISPATCHED, "DISPATCH");
    }

    /** 배차 취소: 배차완료에서 상차완료로 되돌린다. */
    public Order revertToLoaded(Long orderId) {
        return mover.move(orderId, LOADED, "DISPATCH_CANCEL");
    }

    public Order startDelivery(Long orderId) {
        return mover.move(orderId, IN_DELIVERY, "DELIVERY_START");
    }

    /** 배송 완료: 품목별 배송수량을 주문수량으로 기록한다. */
    public Order markDelivered(Long orderId) {
        Order order = mover.move(orderId, DELIVERED, "DELIVER");
        order.getItems().forEach(i -> i.recordDelivered(i.getQuantity()));
        return order;
    }

    public Order markFailed(Long orderId) {
        return mover.move(orderId, FAILED, "DELIVERY_FAIL");
    }

    @Transactional(readOnly = true)
    public OrderStatus statusOf(Long orderId) {
        return orderService.get(orderId).getStatus();
    }
}
