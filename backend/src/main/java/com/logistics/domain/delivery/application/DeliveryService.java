package com.logistics.domain.delivery.application;

import com.logistics.domain.dispatch.application.DispatchCompletionService;
import com.logistics.domain.loading.application.ShipmentService;
import com.logistics.domain.loading.domain.FailReason;
import com.logistics.domain.loading.domain.Shipment;
import com.logistics.domain.loading.domain.ShipmentStatus;
import com.logistics.domain.order.application.OrderService;
import com.logistics.domain.order.application.OrderShippingService;
import com.logistics.domain.order.domain.Order;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/** 배송 완료·실패 처리. 처리 후 배차의 모든 배송이 끝났는지 확인한다. */
@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryService {

    private final ShipmentService shipmentService;
    private final OrderService orderService;
    private final OrderShippingService orderShippingService;
    private final DispatchCompletionService completionService;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

    /** 인도수량이 배송수량(주문수량)과 같을 때만 완료할 수 있다. */
    public Shipment deliver(Long shipmentId, int deliveredQty) {
        Shipment shipment = lockInDelivery(shipmentId);
        Order order = orderService.get(shipment.getOrderId());
        if (deliveredQty != order.getTotalQuantity()) {
            throw new BusinessException(ErrorCode.DELIVERED_QTY_MISMATCH);
        }
        shipment.deliver(LocalDateTime.now(clock));
        orderShippingService.markDelivered(order.getId());
        completionService.completeIfAllClosed(shipment.getDispatchId());
        return shipment;
    }

    public Shipment fail(Long shipmentId, FailReason reason, String detail) {
        Shipment shipment = lockInDelivery(shipmentId);
        Order order = orderService.get(shipment.getOrderId());
        shipment.fail(reason, detail);
        orderShippingService.markFailed(order.getId());
        eventPublisher.publishEvent(new ShipmentFailedEvent(shipment.getId(), order.getId(), reason,
                order.getTotalQuantity()));
        completionService.completeIfAllClosed(shipment.getDispatchId());
        return shipment;
    }

    private Shipment lockInDelivery(Long shipmentId) {
        Shipment shipment = shipmentService.getAllForUpdate(List.of(shipmentId)).get(0);
        if (shipment.getStatus() != ShipmentStatus.IN_DELIVERY) {
            throw new BusinessException(ErrorCode.SHIPMENT_NOT_IN_DELIVERY);
        }
        return shipment;
    }
}
