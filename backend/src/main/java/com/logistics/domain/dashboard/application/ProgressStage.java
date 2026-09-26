package com.logistics.domain.dashboard.application;

import com.logistics.domain.order.domain.OrderStatus;

import java.util.List;

import static com.logistics.domain.order.domain.OrderStatus.*;

/** 물류 진행 현황의 단계와 그 단계에 속하는 주문 상태 (요약 집계와 같은 기준) */
public enum ProgressStage {
    ORDERS(List.of()),
    PICKING(List.of(OUTBOUND_WAITING, OrderStatus.PICKING)),
    PACKING(List.of(PICKED)),
    LOADING(List.of(PACKED)),
    DELIVERY(List.of(IN_DELIVERY));

    /** 비어 있으면 상태 제한 없이 그날 주문 전체 */
    private final List<OrderStatus> statuses;

    ProgressStage(List<OrderStatus> statuses) {
        this.statuses = statuses;
    }

    public List<OrderStatus> statuses() {
        return statuses;
    }
}
