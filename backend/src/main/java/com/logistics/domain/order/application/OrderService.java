package com.logistics.domain.order.application;

import com.logistics.domain.inventory.application.StockReservationService;
import com.logistics.domain.master.application.ProductService;
import com.logistics.domain.order.domain.Order;
import com.logistics.domain.order.domain.OrderItem;
import com.logistics.domain.order.domain.OrderRepository;
import com.logistics.domain.order.domain.OrderSearchCriteria;
import com.logistics.domain.order.domain.OrderStatus;
import com.logistics.global.error.BusinessException;
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

    public Order create(CreateOrderCommand command) {
        Set<Long> productIds = command.items().stream()
                .map(CreateOrderCommand.Line::productId).collect(Collectors.toSet());
        if (productService.getProductMap(productIds).size() != productIds.size()) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        Order order = new Order(command.customerName(), command.address(), command.phone(), LocalDateTime.now(clock));
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

    @Transactional(readOnly = true)
    public java.util.List<Order> findByStatus(OrderStatus status) {
        return orderRepository.search(new OrderSearchCriteria(null, null, status, null, null, 0, 200));
    }

    @Transactional(readOnly = true)
    public java.util.Map<Long, Order> getOrderMap(java.util.Collection<Long> ids) {
        return orderRepository.findAllByIds(ids).stream()
                .collect(Collectors.toMap(Order::getId, java.util.function.Function.identity()));
    }

    @Transactional(readOnly = true)
    public Order get(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }
}
