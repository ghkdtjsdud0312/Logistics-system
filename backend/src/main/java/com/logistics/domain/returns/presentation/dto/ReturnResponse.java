package com.logistics.domain.returns.presentation.dto;

import com.logistics.domain.loading.domain.FailReason;
import com.logistics.domain.master.domain.Product;
import com.logistics.domain.order.domain.Order;
import com.logistics.domain.returns.domain.ReturnOrder;
import com.logistics.domain.returns.domain.ReturnStatus;

import java.util.Map;
import java.util.stream.Collectors;

/** items는 '상품명 × 수량' 요약 문자열이다. */
public record ReturnResponse(
        Long id,
        String returnNo,
        String orderNo,
        String customerName,
        String items,
        FailReason reason,
        int quantity,
        ReturnStatus status,
        Long locationId
) {
    public static ReturnResponse of(ReturnOrder r, Order order, Map<Long, Product> products) {
        String items = order.getItems().stream()
                .map(i -> products.get(i.getProductId()).getName() + " × " + i.getQuantity())
                .collect(Collectors.joining(", "));
        return new ReturnResponse(r.getId(), r.getReturnNo(), order.getOrderNo(), order.getCustomerName(),
                items, r.getReason(), r.getQuantity(), r.getStatus(), r.getLocationId());
    }
}
