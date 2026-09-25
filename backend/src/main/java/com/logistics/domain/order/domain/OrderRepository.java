package com.logistics.domain.order.domain;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(Long id);

    List<Order> findAllByIds(java.util.Collection<Long> ids);

    /** 최신 주문 순 */
    List<Order> search(OrderSearchCriteria criteria);
}
