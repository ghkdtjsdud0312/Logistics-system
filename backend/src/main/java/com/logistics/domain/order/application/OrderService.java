package com.logistics.domain.order.application;

import com.logistics.domain.inventory.application.StockReservationService;
import com.logistics.domain.master.application.ProductService;
import com.logistics.domain.order.domain.Order;
import com.logistics.domain.order.domain.OrderItem;
import com.logistics.domain.order.domain.OrderRepository;
import com.logistics.domain.order.domain.OrderSearchCriteria;
import com.logistics.domain.order.domain.OrderStatus;
import com.logistics.global.error.BusinessException;
import com.logistics.global.geocoding.GeocodingClient;
import com.logistics.global.error.ErrorCode;
import com.logistics.global.event.StatusChangedEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/** 주문 생성과 재고 예약. 재고가 부족하면 주문 전체가 롤백된다. */
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final StockReservationService reservationService;
    private final StatusChangedEventPublisher eventPublisher;
    private final Clock clock;
    private final GeocodingClient geocodingClient;

    public Order create(CreateOrderCommand command) {
        Set<Long> productIds = command.items().stream()
                .map(CreateOrderCommand.Line::productId).collect(Collectors.toSet());
        if (productService.getProductMap(productIds).size() != productIds.size()) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        Order order = new Order(command.customerName(), command.address(), command.phone(), LocalDateTime.now(clock));
        geocodingClient.geocode(order.getAddress()).ifPresent(c -> order.locate(c.latitude(), c.longitude()));
        command.items().forEach(line -> order.addItem(line.productId(), line.quantity()));
        orderRepository.save(order);
        order.assignNo();
        for (OrderItem item : order.getItems()) {
            reservationService.reserve(order.getId(), item.getId(), item.getProductId(), item.getQuantity());
        }
        eventPublisher.publish("ORDER", order.getId(), order.getOrderNo(), order.getId(),
                "CREATE", null, OrderStatus.RECEIVED.name());
        return order;
    }

    /** 주문 시각 [from, to) 구간의 상태별 주문 수 (대시보드 집계용) */
    @Transactional(readOnly = true)
    public java.util.Map<OrderStatus, Long> countByStatus(LocalDateTime from, LocalDateTime to) {
        return orderRepository.countByStatus(from, to);
    }

    @Transactional(readOnly = true)
    public java.util.List<Order> findByStatus(OrderStatus status) {
        return orderRepository.search(new OrderSearchCriteria(null, null, status, null, null, 0, 200));
    }

    @Transactional(readOnly = true)
    public java.util.Map<Long, Order> getOrderMap(java.util.Collection<Long> ids) {
        return orderRepository.findAllByIds(ids).stream()
                .collect(Collectors.toMap(Order::getId, java.util.function.Function.identity()));
    }

    /** 좌표가 없는 주문은 주소로 한 번 더 변환을 시도해 돌려준다. */
    public java.util.Map<Long, Order> getLocatedOrderMap(java.util.Collection<Long> ids) {
        java.util.Map<Long, Order> orders = getOrderMap(ids);
        orders.values().stream().filter(o -> !o.hasCoordinate()).forEach(o ->
                geocodingClient.geocode(o.getAddress()).ifPresent(c -> o.locate(c.latitude(), c.longitude())));
        return orders;
    }

    @Transactional(readOnly = true)
    public Order get(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }
}
