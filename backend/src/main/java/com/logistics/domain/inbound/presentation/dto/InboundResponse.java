package com.logistics.domain.inbound.presentation.dto;

import com.logistics.domain.inbound.domain.Inbound;
import com.logistics.domain.inbound.domain.InboundStatus;
import com.logistics.domain.master.domain.Product;

import java.time.LocalDate;

public record InboundResponse(
        Long id,
        String inboundNo,
        String partnerName,
        Long productId,
        String productCode,
        String productName,
        int quantity,
        LocalDate inboundDate,
        InboundStatus status,
        Long locationId
) {
    public static InboundResponse of(Inbound inbound, Product product) {
        return new InboundResponse(inbound.getId(), inbound.getInboundNo(), inbound.getPartnerName(),
                inbound.getProductId(), product.getCode(), product.getName(), inbound.getQuantity(),
                inbound.getInboundDate(), inbound.getStatus(), inbound.getLocationId());
    }
}
