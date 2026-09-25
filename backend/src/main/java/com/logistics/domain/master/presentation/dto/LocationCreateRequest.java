package com.logistics.domain.master.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record LocationCreateRequest(@NotBlank(message = "위치 코드는 필수입니다.") String code) {
}
