package com.logistics.domain.dashboard.application;

import com.logistics.domain.dashboard.presentation.dto.DashboardSummary;
import com.logistics.domain.order.application.OrderService;
import com.logistics.domain.order.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.logistics.domain.order.domain.OrderStatus.*;

/** 오늘의 물류 현황 집계. 캐시에 있으면 그대로 돌려주고 없으면 DB에서 집계해 캐시에 저장한다. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    static final String SUMMARY_KEY = "summary";

    private final OrderService orderService;
    private final DashboardCache cache;
    private final Clock clock;

    public DashboardSummary getSummary() {
        return getSummary(null);
    }

    /** date가 없거나 오늘이면 캐시를 쓰고, 과거 날짜는 그날 주문의 현재 상태를 캐시 없이 집계한다. */
    public DashboardSummary getSummary(LocalDate date) {
        LocalDate today = LocalDate.now(clock);
        if (date != null && !date.equals(today)) {
            return compute(date);
        }
        return cache.get(SUMMARY_KEY, DashboardSummary.class).orElseGet(() -> {
            DashboardSummary summary = compute(today);
            cache.put(SUMMARY_KEY, summary);
            return summary;
        });
    }

    private DashboardSummary compute(LocalDate date) {
        Map<OrderStatus, Long> counts = orderService.countByStatus(date.atStartOfDay(), date.plusDays(1).atStartOfDay());
        int picking = count(counts, OUTBOUND_WAITING) + count(counts, PICKING);
        int packing = count(counts, PICKED);
        int loading = count(counts, PACKED);
        int inDelivery = count(counts, IN_DELIVERY);
        int total = counts.values().stream().mapToInt(Long::intValue).sum();
        Map<String, Integer> progress = new LinkedHashMap<>();
        progress.put("ORDERS", total);
        progress.put("PICKING", picking);
        progress.put("PACKING", packing);
        progress.put("LOADING", loading);
        progress.put("DELIVERY", inDelivery);
        return new DashboardSummary(date, total, picking, packing, loading, inDelivery,
                count(counts, DELIVERED), count(counts, FAILED), progress);
    }

    private int count(Map<OrderStatus, Long> counts, OrderStatus status) {
        return counts.getOrDefault(status, 0L).intValue();
    }
}
