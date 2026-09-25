package com.logistics.domain.order.application;

import com.logistics.domain.master.application.ProductService;
import com.logistics.domain.master.domain.Product;
import com.logistics.domain.order.domain.Order;
import com.logistics.domain.order.domain.OrderRepository;
import com.logistics.domain.order.domain.OrderSearchCriteria;
import com.logistics.domain.order.presentation.dto.OrderDetailResponse;
import com.logistics.domain.order.domain.OrderStatus;
import com.logistics.domain.order.presentation.dto.OrderDetailResponse.DeliveryInfo;
import com.logistics.domain.order.presentation.dto.OrderDetailResponse.EventRow;
import com.logistics.domain.order.presentation.dto.OrderListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** 주문 목록/상세 조회. 상품 이름은 master 서비스로 조회한다. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryService {

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final ProductService productService;
    private final OrderDeliveryProvider deliveryProvider;
    private final OrderEventProvider eventProvider;

    public List<OrderListResponse> search(OrderSearchCriteria criteria) {
        List<Order> orders = orderRepository.search(criteria);
        Map<Long, Product> products = productMap(orders);
        Map<Long, OrderDeliveryView> deliveries = deliveryProvider.findByOrderIds(
                orders.stream().map(Order::getId).collect(Collectors.toSet()));
        return orders.stream().map(o -> OrderListResponse.of(o, products, statusOf(deliveries.get(o.getId())))).toList();
    }

    public OrderDetailResponse getDetail(Long id) {
        Order order = orderService.get(id);
        OrderDeliveryView view = deliveryProvider.findByOrderIds(List.of(id)).get(id);
        DeliveryInfo delivery = view == null || view.vehicleNumber() == null ? null : new DeliveryInfo(
                view.vehicleNumber(), view.driverName(), view.plannedStartAt(), view.startedAt());
        List<OrderEventView> logs = eventProvider.findByOrderId(id);
        Map<OrderStatus, LocalDateTime> times = new EnumMap<>(OrderStatus.class);
        logs.stream().filter(l -> l.toStatus() != null)
                .forEach(l -> times.putIfAbsent(OrderStatus.valueOf(l.toStatus()), l.at()));
        List<EventRow> events = logs.stream().map(l -> new EventRow(l.at(), l.description())).toList();
        return OrderDetailResponse.of(order, productMap(List.of(order)), OrderTimelineBuilder.build(order, times),
                delivery, events);
    }

    private String statusOf(OrderDeliveryView view) {
        return view == null ? null : view.shipmentStatus();
    }

    private Map<Long, Product> productMap(Collection<Order> orders) {
        return productService.getProductMap(orders.stream()
                .flatMap(o -> o.getItems().stream()).map(i -> i.getProductId()).collect(Collectors.toSet()));
    }
}
