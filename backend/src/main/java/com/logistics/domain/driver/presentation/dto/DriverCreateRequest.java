package com.logistics.domain.driver.presentation.dto;

import com.logistics.domain.driver.domain.Driver;
import jakarta.validation.constraints.NotBlank;

public record DriverCreateRequest(
        @NotBlank(message = "기사 ID는 필수입니다.") String driverCode,
        @NotBlank(message = "기사 이름은 필수입니다.") String name,
        String phone
) {
    public Driver toEntity() {
        return Driver.builder().driverCode(driverCode).name(name).phone(phone).build();
    }
}
