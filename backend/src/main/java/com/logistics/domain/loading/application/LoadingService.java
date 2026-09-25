package com.logistics.domain.loading.application;

import com.logistics.domain.loading.domain.Shipment;
import com.logistics.domain.loading.domain.ShipmentRepository;
import com.logistics.domain.order.application.OrderService;
import com.logistics.domain.order.application.OrderShippingService;
import com.logistics.domain.order.domain.OrderStatus;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** 상차: 포장완료 주문을 상차완료로 바꾸고 주문별 Shipment를 만든다. */
@Service
@RequiredArgsConstructor
@Transactional
public class LoadingService {

    private final ShipmentRepository shipmentRepository;
    private final OrderService orderService;
    private final OrderShippingService orderShippingService;

    public List<Shipment> load(List<Long> orderIds) {
        for (Long orderId : orderIds) {
            if (orderService.get(orderId).getStatus() != OrderStatus.PACKED) {
                throw new BusinessException(ErrorCode.ORDER_NOT_PACKED);
            }
        }
        return orderIds.stream().map(orderId -> {
            orderShippingService.completeLoading(orderId);
            return shipmentRepository.save(new Shipment(orderId));
        }).toList();
    }
}
