package com.logistics.domain.dashboard.application;

import com.logistics.domain.audit.application.AuditQueryService;
import com.logistics.domain.dashboard.presentation.dto.DashboardEvent;
import com.logistics.domain.delivery.application.DeliveryStatusQueryService;
import com.logistics.domain.delivery.presentation.dto.DeliveryStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** 대시보드의 차량 배송 현황과 최근 이벤트 조회 (읽기 전용) */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardQueryService {

    private final DeliveryStatusQueryService deliveryStatusQueryService;
    private final AuditQueryService auditQueryService;

    public List<DeliveryStatusResponse> getVehicles() {
        return deliveryStatusQueryService.getBoard();
    }

    public List<DashboardEvent> getRecentEvents(int limit) {
        return auditQueryService.recent(limit).stream()
                .map(log -> new DashboardEvent(log.occurredAt(), log.targetNo() + " " + log.description()))
                .toList();
    }
}
