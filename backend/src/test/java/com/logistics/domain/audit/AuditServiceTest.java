package com.logistics.domain.audit;

import com.logistics.domain.audit.application.AuditQueryService;
import com.logistics.domain.audit.application.AuditService;
import com.logistics.domain.audit.domain.AuditSearchCriteria;
import com.logistics.domain.audit.infrastructure.AuditConsumer;
import com.logistics.domain.audit.presentation.dto.AuditLogResponse;
import com.logistics.domain.order.application.CreateOrderCommand;
import com.logistics.domain.order.application.CreateOrderCommand.Line;
import com.logistics.domain.order.application.OrderQueryService;
import com.logistics.domain.order.application.OrderService;
import com.logistics.domain.order.presentation.dto.OrderDetailResponse;
import com.logistics.global.event.StatusChangedEvent;
import com.logistics.support.TestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class AuditServiceTest {

    @Autowired private AuditService auditService;
    @Autowired private AuditQueryService queryService;
    @Autowired private OrderService orderService;
    @Autowired private OrderQueryService orderQueryService;
    @Autowired private TestData testData;

    private StatusChangedEvent event(String type, String no, Long orderId, String action, String from, String to,
                                     String actor, Instant at) {
        return new StatusChangedEvent(UUID.randomUUID().toString(), type, 1L, no, orderId, action, from, to, actor, at);
    }

    private List<AuditLogResponse> search(String actor, String target, String action) {
        LocalDate today = LocalDate.now();
        return queryService.search(new AuditSearchCriteria(today, today, actor, target, action, 100));
    }

    @Test
    @DisplayName("이벤트를 기록하고 사용자·대상·작업·기간으로 검색하며 한글 설명을 붙인다")
    void record_andSearch() {
        auditService.record(event("ORDER", "ORD-001", 1L, "DELIVER", "IN_DELIVERY", "DELIVERED", "홍길동", Instant.now()));
        auditService.record(event("ORDER", "ORD-002", 2L, "PICK_COMPLETE", "PICKING", "PICKED", "김작업", Instant.now()));

        assertThat(search(null, null, null)).hasSize(2);
        assertThat(search("홍길동", null, null)).singleElement().satisfies(row -> {
            assertThat(row.targetNo()).isEqualTo("ORD-001");
            assertThat(row.description()).isEqualTo("배송 완료");
            assertThat(row.fromStatus()).isEqualTo("IN_DELIVERY");
            assertThat(row.toStatus()).isEqualTo("DELIVERED");
        });
        assertThat(search(null, "ORD-002", null)).hasSize(1);
        assertThat(search(null, null, "PICK_COMPLETE")).hasSize(1);
        assertThat(queryService.search(new AuditSearchCriteria(
                LocalDate.now().minusDays(3), LocalDate.now().minusDays(2), null, null, null, 100))).isEmpty();
        assertThat(queryService.recent(1)).hasSize(1);
    }

    @Test
    @DisplayName("같은 이벤트를 다시 받아도 감사로그는 한 건만 기록된다")
    void duplicateEvent_recordedOnce() {
        StatusChangedEvent event = event("ORDER", "ORD-009", 9L, "CREATE", null, "RECEIVED", "SYSTEM", Instant.now());
        AuditConsumer consumer = new AuditConsumer(auditService);

        consumer.onMessage(event);
        consumer.onMessage(event);

        assertThat(auditService.record(event)).isFalse();
        assertThat(search(null, "ORD-009", null)).hasSize(1);
    }

    @Test
    @DisplayName("주문 상세의 이벤트 이력과 타임라인 시각이 감사로그로 채워진다")
    void orderDetail_usesAuditLogs() {
        Long productId = testData.product("AUD001", 0.5);
        testData.stock(productId, testData.locations("AU-01").get(0), 10);
        Long orderId = orderService.create(new CreateOrderCommand("김철수", "서울", "010", List.of(new Line(productId, 3)))).getId();
        Instant picked = Instant.parse("2026-09-25T01:02:03Z");
        auditService.record(event("ORDER", "ORD-X", orderId, "PICK_COMPLETE", "PICKING", "PICKED", "김작업", picked));
        auditService.record(event("PICKING_TASK", "PICK-001", orderId, "PICK_COMPLETE", "IN_PROGRESS", "COMPLETED", "김작업",
                picked.minusSeconds(5)));

        OrderDetailResponse detail = orderQueryService.getDetail(orderId);

        assertThat(detail.events()).extracting(OrderDetailResponse.EventRow::description)
                .containsExactly("피킹 작업 완료 (PICK-001)", "피킹 완료");
        assertThat(detail.timeline().get(1).at()).isNotNull();
        assertThat(detail.timeline().get(2).at()).isNull();
    }
}
