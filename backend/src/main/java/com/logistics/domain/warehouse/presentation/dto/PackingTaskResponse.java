package com.logistics.domain.warehouse.presentation.dto;

import com.logistics.domain.master.domain.Product;
import com.logistics.domain.order.domain.Order;
import com.logistics.domain.warehouse.domain.PackingTask;
import com.logistics.domain.warehouse.domain.WorkStatus;

import java.util.Map;
import java.util.stream.Collectors;

/** items는 '상품명 × 수량' 요약 문자열이다. */
public record PackingTaskResponse(
        Long id,
        String taskNo,
        String orderNo,
        String items,
        String boxCode,
        WorkStatus status
) {
    public static PackingTaskResponse of(PackingTask task, Order order, Map<Long, Product> products) {
        String items = order.getItems().stream()
                .map(i -> products.get(i.getProductId()).getName() + " × " + i.getQuantity())
                .collect(Collectors.joining(", "));
        return new PackingTaskResponse(task.getId(), task.getTaskNo(), order.getOrderNo(),
                items, task.getBoxCode(), task.getStatus());
    }
}
