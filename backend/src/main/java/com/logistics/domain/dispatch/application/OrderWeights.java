package com.logistics.domain.dispatch.application;

import com.logistics.domain.master.domain.Product;
import com.logistics.domain.order.domain.Order;

import java.util.Map;

/** 주문 총 중량(kg) = 품목 수량 × 상품 단위 중량. 적재량 검증과 화면 표시에 쓰는 순수 함수 */
public final class OrderWeights {

    private OrderWeights() {
    }

    public static double of(Order order, Map<Long, Product> products) {
        return order.getItems().stream()
                .mapToDouble(i -> i.getQuantity() * products.get(i.getProductId()).getUnitWeightKg())
                .sum();
    }
}
