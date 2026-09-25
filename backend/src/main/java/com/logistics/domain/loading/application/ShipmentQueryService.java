package com.logistics.domain.loading.application;

import com.logistics.domain.dispatch.application.OrderWeights;
import com.logistics.domain.loading.domain.Shipment;
import com.logistics.domain.loading.domain.ShipmentRepository;
import com.logistics.domain.loading.domain.ShipmentStatus;
import com.logistics.domain.loading.presentation.dto.ShipmentResponse;
import com.logistics.domain.master.application.ProductService;
import com.logistics.domain.master.domain.Product;
import com.logistics.domain.order.application.OrderService;
import com.logistics.domain.order.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Shipment 목록 조회 (주문 정보와 총 중량 포함) */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShipmentQueryService {

    private final ShipmentRepository shipmentRepository;
    private final OrderService orderService;
    private final ProductService productService;

    public List<ShipmentResponse> search(ShipmentStatus status, Long dispatchId) {
        return toResponses(shipmentRepository.search(status, dispatchId));
    }

    public List<ShipmentResponse> toResponses(List<Shipment> shipments) {
        Map<Long, Order> orders = orderService.getOrderMap(
                shipments.stream().map(Shipment::getOrderId).collect(Collectors.toSet()));
        Map<Long, Product> products = productService.getProductMap(orders.values().stream()
                .flatMap(o -> o.getItems().stream()).map(i -> i.getProductId()).collect(Collectors.toSet()));
        return shipments.stream().map(s -> {
            Order order = orders.get(s.getOrderId());
            return ShipmentResponse.of(s, order, OrderWeights.of(order, products));
        }).toList();
    }
}
