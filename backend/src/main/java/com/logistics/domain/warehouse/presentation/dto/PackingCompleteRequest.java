package com.logistics.domain.warehouse.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record PackingCompleteRequest(@NotBlank(message = "박스 코드는 필수입니다.") String boxCode) {
}
