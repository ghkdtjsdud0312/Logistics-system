package com.logistics.domain.driver.presentation.dto;

import com.logistics.domain.driver.domain.Driver;
import com.logistics.domain.driver.domain.DriverStatus;

public record DriverResponse(
        Long id,
        String name,
        DriverStatus status
) {
    public static DriverResponse from(Driver driver) {
        return new DriverResponse(driver.getId(), driver.getName(), driver.getStatus());
    }
}
