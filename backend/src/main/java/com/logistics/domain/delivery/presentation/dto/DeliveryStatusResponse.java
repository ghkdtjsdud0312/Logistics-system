package com.logistics.domain.delivery.presentation.dto;

import com.logistics.domain.dispatch.domain.DispatchStatus;
import com.logistics.domain.dispatch.presentation.dto.DispatchResponse;
import com.logistics.domain.loading.domain.ShipmentStatus;
import com.logistics.domain.loading.presentation.dto.ShipmentResponse;

import java.time.LocalDateTime;
import java.util.List;

/** 차량 중심 배송현황. completed는 배송완료 건수, failed는 배송실패 건수다. */
public record DeliveryStatusResponse(
        Long dispatchId,
        String dispatchNo,
        String vehicleNumber,
        String driverName,
        DispatchStatus status,
        int completed,
        int failed,
        int total,
        LocalDateTime startedAt,
        List<OrderRow> orders
) {
    public record OrderRow(Long shipmentId, String orderNo, String customerName, ShipmentStatus status) {
    }

    public static DeliveryStatusResponse of(DispatchResponse dispatch, List<ShipmentResponse> shipments) {
        int completed = (int) shipments.stream().filter(s -> s.status() == ShipmentStatus.DELIVERED).count();
        int failed = (int) shipments.stream().filter(s -> s.status() == ShipmentStatus.FAILED).count();
        return new DeliveryStatusResponse(dispatch.id(), dispatch.dispatchNo(), dispatch.vehicleNumber(),
                dispatch.driverName(), dispatch.status(), completed, failed, shipments.size(), dispatch.startedAt(),
                shipments.stream().map(s -> new OrderRow(s.id(), s.orderNo(), s.customerName(), s.status())).toList());
    }
}
