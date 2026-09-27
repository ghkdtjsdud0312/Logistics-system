package com.logistics.domain.dispatch.application;

import com.logistics.domain.loading.domain.Shipment;
import com.logistics.domain.master.application.ProductService;
import com.logistics.domain.master.domain.Product;
import com.logistics.domain.order.application.OrderService;
import com.logistics.domain.order.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/** Shipment 묶음의 총 중량(kg) 계산 */
@Component
@RequiredArgsConstructor
public class DispatchWeightCalculator {

    private final OrderService orderService;
    private final ProductService productService;

    public double of(Collection<Shipment> shipments) {
        Map<Long, Order> orders = orderService.getOrderMap(
                shipments.stream().map(Shipment::getOrderId).collect(Collectors.toSet()));
        Map<Long, Product> products = productService.getProductMap(orders.values().stream()
                .flatMap(o -> o.getItems().stream()).map(i -> i.getProductId()).collect(Collectors.toSet()));
        return orders.values().stream().mapToDouble(o -> OrderWeights.of(o, products)).sum();
    }
}
