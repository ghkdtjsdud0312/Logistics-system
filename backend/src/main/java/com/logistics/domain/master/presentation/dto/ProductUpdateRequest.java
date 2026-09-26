package com.logistics.domain.master.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ProductUpdateRequest(
        @NotBlank(message = "상품명은 필수입니다.") String name,
        @NotBlank(message = "단위는 필수입니다.") String unit,
        @Positive(message = "단위 중량은 0보다 커야 합니다.") double unitWeightKg
) {
}
