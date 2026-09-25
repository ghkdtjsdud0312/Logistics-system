package com.logistics.domain.order.presentation.dto;

import com.logistics.domain.order.domain.Order;
import com.logistics.domain.order.domain.OrderStatus;

public record OrderCreateResponse(Long id, String orderNo, OrderStatus status) {

    public static OrderCreateResponse from(Order order) {
        return new OrderCreateResponse(order.getId(), order.getOrderNo(), order.getStatus());
    }
}
