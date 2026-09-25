package com.logistics.domain.driver.presentation.dto;

import com.logistics.domain.driver.domain.DriverStatus;
import jakarta.validation.constraints.NotNull;

public record DriverStatusRequest(@NotNull(message = "상태는 필수입니다.") DriverStatus status) {
}
