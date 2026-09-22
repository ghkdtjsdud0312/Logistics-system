package com.logistics.domain.delivery.application;

import com.logistics.domain.delivery.domain.*;
import com.logistics.domain.delivery.infrastructure.DeliveryEventProducer;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 배송/관제 유스케이스
 * - 위치 갱신 시 Kafka로 이벤트를 발행하고, 구독중인 SSE 클라이언트는
 *   DeliveryEventListener(Consumer)를 통해 실시간으로 전달받음
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryEventProducer deliveryEventProducer;

    @Transactional
    public Delivery createDelivery(Long dispatchId) {
        Delivery delivery = Delivery.builder().dispatchId(dispatchId).build();
        return deliveryRepository.save(delivery);
    }

    public Delivery getDelivery(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DELIVERY_NOT_FOUND));
    }

    @Transactional
    public Delivery updateLocation(Long id, double latitude, double longitude) {
        Delivery delivery = getDelivery(id);
        delivery.updateLocation(latitude, longitude);

        deliveryEventProducer.publish(new DeliveryLocationEvent(
                delivery.getId(),
                delivery.getDispatchId(),
                delivery.getStatus(),
                latitude,
                longitude
        ));

        return delivery;
    }
}
