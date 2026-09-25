package com.logistics.domain.order.presentation.dto;

import com.logistics.domain.master.domain.Product;
import com.logistics.domain.order.application.OrderTimelineBuilder;
import com.logistics.domain.order.domain.Order;
import com.logistics.domain.order.domain.OrderItem;
import com.logistics.domain.order.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/** 주문 상세. delivery는 배차 이후, events는 감사로그 연동 이후에 채워진다. */
public record OrderDetailResponse(
        Long id,
        String orderNo,
        OrderStatus status,
        String customerName,
        String address,
        String phone,
        LocalDateTime orderedAt,
        List<ItemRow> items,
        List<TimelineStep> timeline,
        DeliveryInfo delivery,
        List<EventRow> events
) {
    public record ItemRow(String productCode, String productName,
                          int orderedQty, int pickedQty, int loadedQty, int deliveredQty) {
    }

    public record DeliveryInfo(String vehicleNumber, String driverName,
                               LocalDateTime plannedStartAt, LocalDateTime startedAt) {
    }

    public record EventRow(LocalDateTime at, String description) {
    }

    public static OrderDetailResponse of(Order order, Map<Long, Product> products,
                                         DeliveryInfo delivery, List<EventRow> events) {
        return new OrderDetailResponse(order.getId(), order.getOrderNo(), order.getStatus(),
                order.getCustomerName(), order.getAddress(), order.getPhone(), order.getOrderedAt(),
                order.getItems().stream().map(i -> itemRow(i, products.get(i.getProductId()))).toList(),
                OrderTimelineBuilder.build(order), delivery, events);
    }

    private static ItemRow itemRow(OrderItem item, Product product) {
        return new ItemRow(product.getCode(), product.getName(), item.getQuantity(),
                item.getPickedQty(), item.getLoadedQty(), item.getDeliveredQty());
    }
}
