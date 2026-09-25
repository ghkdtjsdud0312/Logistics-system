package com.logistics.domain.order.infrastructure;

import com.logistics.domain.order.domain.Order;
import com.logistics.domain.order.domain.OrderSearchCriteria;
import org.springframework.data.jpa.domain.Specification;

/** 주문 검색 조건을 JPA Specification으로 변환한다. */
final class OrderSpecifications {

    private OrderSpecifications() {
    }

    static Specification<Order> from(OrderSearchCriteria c) {
        return Specification.<Order>where(null)
                .and(blank(c.orderNo()) ? null : (r, q, cb) -> cb.like(r.get("orderNo"), "%" + c.orderNo().trim() + "%"))
                .and(blank(c.customerName()) ? null : (r, q, cb) -> cb.like(r.get("customerName"), "%" + c.customerName().trim() + "%"))
                .and(c.status() == null ? null : (r, q, cb) -> cb.equal(r.get("status"), c.status()))
                .and(c.from() == null ? null : (r, q, cb) -> cb.greaterThanOrEqualTo(r.get("orderedAt"), c.from().atStartOfDay()))
                .and(c.to() == null ? null : (r, q, cb) -> cb.lessThan(r.get("orderedAt"), c.to().plusDays(1).atStartOfDay()));
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
