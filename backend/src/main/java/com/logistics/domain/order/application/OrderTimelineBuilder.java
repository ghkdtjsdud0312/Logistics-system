package com.logistics.domain.order.application;

import com.logistics.domain.order.domain.Order;
import com.logistics.domain.order.domain.OrderStatus;
import com.logistics.domain.order.presentation.dto.TimelineStep;

import java.util.List;

import static com.logistics.domain.order.domain.OrderStatus.*;

/** 주문 상세의 물류 진행 타임라인을 계산하는 순수 함수 */
public final class OrderTimelineBuilder {

    private static final List<OrderStatus> STEPS =
            List.of(RECEIVED, PICKED, PACKED, LOADED, DISPATCHED, IN_DELIVERY, DELIVERED);

    private OrderTimelineBuilder() {
    }

    public static List<TimelineStep> build(Order order) {
        return STEPS.stream()
                .map(step -> new TimelineStep(step, isDone(order.getStatus(), step),
                        step == RECEIVED ? order.getOrderedAt() : null))
                .toList();
    }

    /** 배송실패 주문은 배송중 단계까지만 완료로 본다. */
    private static boolean isDone(OrderStatus current, OrderStatus step) {
        if (current == FAILED) {
            return step.ordinal() <= IN_DELIVERY.ordinal();
        }
        return current.ordinal() >= step.ordinal();
    }
}
