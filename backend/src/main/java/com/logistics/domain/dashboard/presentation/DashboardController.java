package com.logistics.domain.dashboard.presentation;

import com.logistics.domain.dashboard.application.DashboardQueryService;
import com.logistics.domain.dashboard.application.DashboardService;
import com.logistics.domain.dashboard.application.ProgressStage;
import com.logistics.domain.order.presentation.dto.OrderListResponse;
import com.logistics.domain.dashboard.presentation.dto.DashboardEvent;
import com.logistics.domain.dashboard.presentation.dto.DashboardSummary;
import com.logistics.domain.delivery.presentation.dto.DeliveryStatusResponse;
import com.logistics.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "대시보드", description = "오늘의 물류 현황, 차량 배송 현황, 최근 이벤트 API")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final DashboardQueryService queryService;

    @GetMapping("/summary")
    public ApiResponse<DashboardSummary> summary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.success(dashboardService.getSummary(date));
    }

    @GetMapping("/vehicles")
    public ApiResponse<List<DeliveryStatusResponse>> vehicles() {
        return ApiResponse.success(queryService.getVehicles());
    }

    @GetMapping("/progress/orders")
    public ApiResponse<List<OrderListResponse>> progressOrders(
            @RequestParam ProgressStage stage,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.success(queryService.getProgressOrders(stage, date));
    }

    @GetMapping("/events")
    public ApiResponse<List<DashboardEvent>> events(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.success(queryService.getRecentEvents(limit, date));
    }
}
