package com.logistics.domain.loading.presentation.dto;

import com.logistics.domain.loading.domain.Shipment;
import com.logistics.domain.loading.domain.ShipmentStatus;
import com.logistics.domain.order.domain.Order;

/** 배송 단위 목록 행. totalWeightKg는 배차 적재량 판단에 쓰인다. */
public record ShipmentResponse(
        Long id,
        Long orderId,
        String orderNo,
        String customerName,
        String address,
        int quantity,
        double totalWeightKg,
        ShipmentStatus status,
        Long dispatchId
) {
    public static ShipmentResponse of(Shipment shipment, Order order, double totalWeightKg) {
        return new ShipmentResponse(shipment.getId(), order.getId(), order.getOrderNo(), order.getCustomerName(),
                order.getAddress(), order.getTotalQuantity(), totalWeightKg, shipment.getStatus(),
                shipment.getDispatchId());
    }
}
