package com.logistics.domain.master.presentation.dto;

import com.logistics.domain.master.domain.Product;

public record ProductResponse(Long id, String code, String name, String unit, double unitWeightKg, boolean active) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getCode(), product.getName(),
                product.getUnit(), product.getUnitWeightKg(), product.isActive());
    }
}
