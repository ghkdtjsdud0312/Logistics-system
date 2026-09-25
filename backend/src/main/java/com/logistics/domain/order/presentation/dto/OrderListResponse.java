package com.logistics.domain.order.presentation.dto;

import com.logistics.domain.master.domain.Product;
import com.logistics.domain.order.domain.Order;
import com.logistics.domain.order.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.Map;

/** 주문 목록 행. deliveryStatus는 배송이 시작된 주문만 값이 있다. */
public record OrderListResponse(
        Long id,
        String orderNo,
        String customerName,
        String productSummary,
        int quantity,
        LocalDateTime orderedAt,
        OrderStatus status,
        String deliveryStatus
) {
    public static OrderListResponse of(Order order, Map<Long, Product> products, String deliveryStatus) {
        String first = products.get(order.getItems().get(0).getProductId()).getName();
        int others = order.getItems().size() - 1;
        return new OrderListResponse(order.getId(), order.getOrderNo(), order.getCustomerName(),
                others > 0 ? first + " 외 " + others + "건" : first, order.getTotalQuantity(),
                order.getOrderedAt(), order.getStatus(), deliveryStatus);
    }
}
