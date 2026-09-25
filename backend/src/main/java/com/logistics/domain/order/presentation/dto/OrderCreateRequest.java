package com.logistics.domain.order.presentation.dto;

import com.logistics.domain.order.application.CreateOrderCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record OrderCreateRequest(
        @NotBlank(message = "고객명은 필수입니다.") String customerName,
        @NotBlank(message = "배송 주소는 필수입니다.") String address,
        @NotBlank(message = "연락처는 필수입니다.") String phone,
        @Valid @NotEmpty(message = "주문 상품은 1개 이상이어야 합니다.") List<ItemRequest> items
) {

    public record ItemRequest(
            @NotNull(message = "상품은 필수입니다.") Long productId,
            @Positive(message = "수량은 0보다 커야 합니다.") int quantity
    ) {
    }

    public CreateOrderCommand toCommand() {
        return new CreateOrderCommand(customerName, address, phone,
                items.stream().map(i -> new CreateOrderCommand.Line(i.productId(), i.quantity())).toList());
    }
}
