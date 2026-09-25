package com.logistics.domain.driver.presentation.dto;

import com.logistics.domain.driver.domain.Driver;
import com.logistics.domain.driver.domain.DriverStatus;

public record DriverResponse(
        Long id,
        String driverCode,
        String name,
        String phone,
        DriverStatus status
) {
    public static DriverResponse from(Driver driver) {
        return new DriverResponse(driver.getId(), driver.getDriverCode(),
                driver.getName(), driver.getPhone(), driver.getStatus());
    }
}
