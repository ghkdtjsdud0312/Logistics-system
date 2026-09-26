package com.logistics.domain.driver.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record DriverUpdateRequest(
        @NotBlank(message = "기사 이름은 필수입니다.") String name,
        String phone
) {
}
