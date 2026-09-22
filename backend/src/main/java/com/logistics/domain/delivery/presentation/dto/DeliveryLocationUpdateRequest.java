package com.logistics.domain.delivery.presentation.dto;

public record DeliveryLocationUpdateRequest(
        double latitude,
        double longitude
) {
}
