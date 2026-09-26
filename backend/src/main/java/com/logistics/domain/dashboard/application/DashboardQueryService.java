package com.logistics.domain.dashboard.application;

import com.logistics.domain.audit.application.AuditQueryService;
import com.logistics.domain.dashboard.presentation.dto.DashboardEvent;
import com.logistics.domain.delivery.application.DeliveryStatusQueryService;
import com.logistics.domain.delivery.presentation.dto.DeliveryStatusResponse;
import com.logistics.domain.order.application.OrderQueryService;
import com.logistics.domain.order.domain.OrderSearchCriteria;
import com.logistics.domain.order.domain.OrderStatus;
import com.logistics.domain.order.presentation.dto.OrderListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/** 대시보드의 차량 배송 현황과 최근 이벤트 조회 (읽기 전용) */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardQueryService {

    private final DeliveryStatusQueryService deliveryStatusQueryService;
    private final AuditQueryService auditQueryService;
    private final OrderQueryService orderQueryService;
    private final Clock clock;

    private static final int ORDER_LIMIT = 200;

    /** 진행 현황 단계에 속하는 그날 주문을 최신순으로 돌려준다. date가 없으면 오늘. */
    public List<OrderListResponse> getProgressOrders(ProgressStage stage, LocalDate date) {
        LocalDate day = date == null ? LocalDate.now(clock) : date;
        List<OrderStatus> statuses = stage.statuses();
        if (statuses.isEmpty()) {
            return search(null, day);
        }
        return statuses.stream().flatMap(s -> search(s, day).stream())
                .sorted(Comparator.comparing(OrderListResponse::orderedAt).reversed())
                .toList();
    }

    private List<OrderListResponse> search(OrderStatus status, LocalDate day) {
        return orderQueryService.search(new OrderSearchCriteria(null, null, status, day, day, 0, ORDER_LIMIT));
    }

    public List<DeliveryStatusResponse> getVehicles() {
        return deliveryStatusQueryService.getBoard();
    }

    public List<DashboardEvent> getRecentEvents(int limit) {
        return getRecentEvents(limit, null);
    }

    /** date가 있으면 그날의 이벤트만 최신순으로 돌려준다. */
    public List<DashboardEvent> getRecentEvents(int limit, LocalDate date) {
        return auditQueryService.recent(limit, date).stream()
                .map(log -> new DashboardEvent(log.occurredAt(), log.targetNo() + " " + log.description()))
                .toList();
    }
}
