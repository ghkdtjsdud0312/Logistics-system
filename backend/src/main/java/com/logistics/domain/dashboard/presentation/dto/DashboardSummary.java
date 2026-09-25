package com.logistics.domain.dashboard.presentation.dto;

import java.time.LocalDate;
import java.util.Map;

/**
 * 오늘의 물류 현황. 집계 기준은 오늘 주문일이다.
 * progress는 단계별 현재 건수(ORDERS, PICKING, PACKING, LOADING, DELIVERY)다.
 */
public record DashboardSummary(
        LocalDate date,
        int orders,
        int pickingWaiting,
        int packingWaiting,
        int loadingWaiting,
        int inDelivery,
        int delivered,
        int failed,
        Map<String, Integer> progress
) {
}
