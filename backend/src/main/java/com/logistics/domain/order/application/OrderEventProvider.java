package com.logistics.domain.order.application;

import java.util.List;

/** 주문 상세가 이벤트 이력을 얻는 포트. 구현은 audit 도메인이 제공한다. */
public interface OrderEventProvider {

    /** 오래된 순 */
    List<OrderEventView> findByOrderId(Long orderId);
}
