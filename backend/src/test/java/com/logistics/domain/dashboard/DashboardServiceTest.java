package com.logistics.domain.dashboard;

import com.logistics.domain.dashboard.application.DashboardCache;
import com.logistics.domain.dashboard.application.DashboardQueryService;
import com.logistics.domain.dashboard.application.DashboardService;
import com.logistics.domain.dashboard.application.ProgressStage;
import com.logistics.domain.dashboard.presentation.dto.DashboardSummary;
import com.logistics.domain.delivery.application.DeliveryService;
import com.logistics.domain.audit.application.AuditService;
import com.logistics.domain.loading.domain.FailReason;
import com.logistics.domain.order.application.CreateOrderCommand;
import com.logistics.domain.order.application.CreateOrderCommand.Line;
import com.logistics.domain.order.application.OrderService;
import com.logistics.global.event.StatusChangedEvent;
import com.logistics.support.TestData;
import com.logistics.support.TestFlow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class DashboardServiceTest {

    @Autowired private DashboardService dashboardService;
    @Autowired private DashboardQueryService queryService;
    @Autowired private DashboardCache cache;
    @Autowired private OrderService orderService;
    @Autowired private DeliveryService deliveryService;
    @Autowired private AuditService auditService;
    @Autowired private TestData testData;
    @Autowired private TestFlow testFlow;

    @BeforeEach
    void clearCache() {
        cache.evictAll();
    }

    private void newOrder(String code) {
        Long productId = testData.product(code, 0.5);
        testData.stock(productId, testData.locations("D-" + code).get(0), 5);
        orderService.create(new CreateOrderCommand("고객", "주소", "010", List.of(new Line(productId, 1))));
    }

    @Test
    @DisplayName("오늘 주문을 상태별로 집계하고 단계별 진행 건수를 계산한다")
    void summary_countsByStatus() {
        newOrder("DASH1");
        testFlow.packedOrder(0.5, 2);
        Long delivered = testFlow.deliveringShipment(3);
        Long failed = testFlow.deliveringShipment(1);
        deliveryService.deliver(delivered, 3);
        deliveryService.fail(failed, FailReason.CUSTOMER_ABSENT, "부재");

        DashboardSummary summary = dashboardService.getSummary();

        assertThat(summary.orders()).isEqualTo(4);
        assertThat(summary.loadingWaiting()).isEqualTo(1);
        assertThat(summary.delivered()).isEqualTo(1);
        assertThat(summary.failed()).isEqualTo(1);
        assertThat(summary.inDelivery()).isZero();
        assertThat(summary.progress()).containsEntry("ORDERS", 4).containsEntry("LOADING", 1);
    }

    @Test
    @DisplayName("요약은 캐시에서 반환되고 캐시를 비우면 최신 값으로 다시 집계된다")
    void summary_cacheHit_andEvict() {
        newOrder("DASH2");
        DashboardSummary first = dashboardService.getSummary();
        newOrder("DASH3");

        assertThat(dashboardService.getSummary().orders()).isEqualTo(first.orders());
        cache.evictAll();
        assertThat(dashboardService.getSummary().orders()).isEqualTo(first.orders() + 1);
    }

    @Test
    @DisplayName("최근 이벤트는 감사로그 최신순으로 '대상 + 한글 작업' 문구를 돌려준다")
    void recentEvents() {
        auditService.record(new StatusChangedEvent("e-a", "ORDER", 1L, "ORD-1021", 1L, "DELIVER",
                "IN_DELIVERY", "DELIVERED", "홍길동", Instant.now()));

        assertThat(queryService.getRecentEvents(10)).hasSize(1)
                .first().extracting("description").isEqualTo("ORD-1021 배송 완료");
    }

    @Test
    @DisplayName("과거 날짜를 주면 그날 주문 기준으로 집계하고 캐시를 쓰지 않는다")
    void summary_pastDate() {
        newOrder("DASH4");
        LocalDate today = LocalDate.now();

        assertThat(dashboardService.getSummary(today.minusDays(1)).orders()).isZero();
        assertThat(dashboardService.getSummary(today.minusDays(1)).date()).isEqualTo(today.minusDays(1));
        assertThat(dashboardService.getSummary(today).orders()).isEqualTo(1);
    }

    @Test
    @DisplayName("이벤트는 날짜를 주면 그날 것만 돌려준다")
    void recentEvents_byDate() {
        auditService.record(new StatusChangedEvent("e-b", "ORDER", 1L, "ORD-1", 1L, "DELIVER",
                "IN_DELIVERY", "DELIVERED", "홍길동", Instant.now()));

        assertThat(queryService.getRecentEvents(10, LocalDate.now())).hasSize(1);
        assertThat(queryService.getRecentEvents(10, LocalDate.now().minusDays(1))).isEmpty();
    }

    @Test
    @DisplayName("진행 현황 단계별 주문 목록은 요약 건수와 같고 다른 날짜에는 비어 있다")
    void progressOrders_matchSummary() {
        newOrder("DASH5");
        testFlow.packedOrder(0.5, 2);

        assertThat(queryService.getProgressOrders(ProgressStage.ORDERS, null)).hasSize(2);
        assertThat(queryService.getProgressOrders(ProgressStage.LOADING, null))
                .hasSize(dashboardService.getSummary().loadingWaiting());
        assertThat(queryService.getProgressOrders(ProgressStage.PICKING, null))
                .hasSize(dashboardService.getSummary().pickingWaiting());
        assertThat(queryService.getProgressOrders(ProgressStage.ORDERS, LocalDate.now().minusDays(1))).isEmpty();
    }
}
