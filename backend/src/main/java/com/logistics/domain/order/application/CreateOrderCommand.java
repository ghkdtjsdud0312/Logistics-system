package com.logistics.domain.order.application;

import java.util.List;

/** 주문 생성 입력 */
public record CreateOrderCommand(String customerName, String address, String phone, List<Line> items) {

    public record Line(Long productId, int quantity) {
    }
}
