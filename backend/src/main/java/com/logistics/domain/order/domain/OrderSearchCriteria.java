package com.logistics.domain.order.domain;

import java.time.LocalDate;

/** 주문 목록 검색 조건. null인 조건은 무시한다. */
public record OrderSearchCriteria(
        String orderNo,
        String customerName,
        OrderStatus status,
        LocalDate from,
        LocalDate to,
        int page,
        int size
) {
}
