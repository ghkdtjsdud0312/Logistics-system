package com.logistics.domain.order.application;

import com.logistics.domain.master.application.ProductService;
import com.logistics.domain.master.domain.Product;
import com.logistics.domain.order.domain.Order;
import com.logistics.domain.order.domain.OrderRepository;
import com.logistics.domain.order.domain.OrderSearchCriteria;
import com.logistics.domain.order.presentation.dto.OrderDetailResponse;
import com.logistics.domain.order.presentation.dto.OrderListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
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

    public List<OrderListResponse> search(OrderSearchCriteria criteria) {
        List<Order> orders = orderRepository.search(criteria);
        Map<Long, Product> products = productMap(orders);
        return orders.stream().map(o -> OrderListResponse.of(o, products, null)).toList();
    }

    public OrderDetailResponse getDetail(Long id) {
        Order order = orderService.get(id);
        return OrderDetailResponse.of(order, productMap(List.of(order)), null, List.of());
    }

    private Map<Long, Product> productMap(Collection<Order> orders) {
        return productService.getProductMap(orders.stream()
                .flatMap(o -> o.getItems().stream()).map(i -> i.getProductId()).collect(Collectors.toSet()));
    }
}
