package com.logistics.domain.warehouse.presentation.dto;

import com.logistics.domain.master.application.LocationInfo;
import com.logistics.domain.master.domain.Product;
import com.logistics.domain.order.domain.Order;
import com.logistics.domain.warehouse.domain.PickingTask;
import com.logistics.domain.warehouse.domain.WorkStatus;

public record PickingTaskResponse(
        Long id,
        String taskNo,
        String orderNo,
        String locationCode,
        String productName,
        int requestedQty,
        int pickedQty,
        WorkStatus status
) {
    public static PickingTaskResponse of(PickingTask task, Order order, Product product, LocationInfo location) {
        return new PickingTaskResponse(task.getId(), task.getTaskNo(), order.getOrderNo(),
                location.locationCode(), product.getName(), task.getRequestedQty(),
                task.getPickedQty(), task.getStatus());
    }
}
