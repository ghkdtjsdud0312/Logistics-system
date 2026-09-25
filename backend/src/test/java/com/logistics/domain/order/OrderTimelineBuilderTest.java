package com.logistics.domain.order;

import com.logistics.domain.order.application.OrderTimelineBuilder;
import com.logistics.domain.order.domain.Order;
import com.logistics.domain.order.domain.OrderStatus;
import com.logistics.domain.order.presentation.dto.TimelineStep;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTimelineBuilderTest {

    private Order orderIn(OrderStatus status) {
        Order order = new Order("김철수", "서울", "010", LocalDateTime.of(2026, 9, 25, 9, 21));
        ReflectionTestUtils.setField(order, "status", status);
        return order;
    }

    private long doneCount(Order order) {
        return OrderTimelineBuilder.build(order, java.util.Map.of()).stream().filter(TimelineStep::done).count();
    }

    @Test
    @DisplayName("타임라인은 7단계이고 현재 상태까지 완료로 표시한다")
    void progress() {
        assertThat(OrderTimelineBuilder.build(orderIn(OrderStatus.RECEIVED), java.util.Map.of())).hasSize(7);
        assertThat(doneCount(orderIn(OrderStatus.RECEIVED))).isEqualTo(1);
        assertThat(doneCount(orderIn(OrderStatus.PICKING))).isEqualTo(1);
        assertThat(doneCount(orderIn(OrderStatus.PICKED))).isEqualTo(2);
        assertThat(doneCount(orderIn(OrderStatus.IN_DELIVERY))).isEqualTo(6);
        assertThat(doneCount(orderIn(OrderStatus.DELIVERED))).isEqualTo(7);
    }

    @Test
    @DisplayName("배송실패 주문은 배송완료 단계가 완료로 표시되지 않고 주문 시각은 접수 단계에 표시된다")
    void failed() {
        Order order = orderIn(OrderStatus.FAILED);
        List<TimelineStep> steps = OrderTimelineBuilder.build(order, java.util.Map.of());

        assertThat(steps.get(6).done()).isFalse();
        assertThat(steps.get(5).done()).isTrue();
        assertThat(steps.get(0).at()).isEqualTo(order.getOrderedAt());
    }
}
