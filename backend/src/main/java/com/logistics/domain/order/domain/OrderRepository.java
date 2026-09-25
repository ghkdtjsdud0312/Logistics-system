package com.logistics.domain.order.domain;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(Long id);

    List<Order> findAllByIds(java.util.Collection<Long> ids);

    /** 주문 시각 [from, to) 구간의 상태별 주문 수 */
    java.util.Map<OrderStatus, Long> countByStatus(java.time.LocalDateTime from, java.time.LocalDateTime to);

    /** 최신 주문 순 */
    List<Order> search(OrderSearchCriteria criteria);
}
