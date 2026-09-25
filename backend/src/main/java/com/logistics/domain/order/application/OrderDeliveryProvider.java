package com.logistics.domain.order.application;

import java.util.Collection;
import java.util.Map;

/**
 * 주문 조회가 배송 정보를 얻는 포트. 주문 도메인이 배송 도메인에 의존하지 않도록
 * 인터페이스는 order가 정의하고 구현은 delivery가 제공한다.
 */
public interface OrderDeliveryProvider {

    /** 주문 ID → 배송 정보. 상차 전인 주문은 결과에 없다. */
    Map<Long, OrderDeliveryView> findByOrderIds(Collection<Long> orderIds);
}
