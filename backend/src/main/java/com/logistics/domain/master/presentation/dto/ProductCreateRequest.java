package com.logistics.domain.master.presentation.dto;

import com.logistics.domain.master.domain.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ProductCreateRequest(
        @NotBlank(message = "상품코드는 필수입니다.") String code,
        @NotBlank(message = "상품명은 필수입니다.") String name,
        @NotBlank(message = "단위는 필수입니다.") String unit,
        @Positive(message = "단위 중량은 0보다 커야 합니다.") double unitWeightKg
) {
    public Product toEntity() {
        return Product.builder().code(code).name(name).unit(unit).unitWeightKg(unitWeightKg).build();
    }
}
