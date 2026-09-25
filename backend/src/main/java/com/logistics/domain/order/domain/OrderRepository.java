package com.logistics.domain.order.domain;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(Long id);

    /** 최신 주문 순 */
    List<Order> search(OrderSearchCriteria criteria);
}
