package com.logistics.domain.delivery.presentation.dto;

import com.logistics.domain.delivery.domain.Delivery;
import com.logistics.domain.delivery.domain.DeliveryStatus;

public record DeliveryResponse(
        Long id,
        Long dispatchId,
        DeliveryStatus status,
        double currentLatitude,
        double currentLongitude
) {
    public static DeliveryResponse from(Delivery delivery) {
        return new DeliveryResponse(
                delivery.getId(),
                delivery.getDispatchId(),
                delivery.getStatus(),
                delivery.getCurrentLatitude(),
                delivery.getCurrentLongitude()
        );
    }
}
