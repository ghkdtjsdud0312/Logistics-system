package com.logistics.domain.order;

import com.logistics.domain.order.domain.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.logistics.domain.order.domain.OrderStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

class OrderStatusTest {

    @Test
    @DisplayName("주문은 정해진 순서대로만 다음 상태로 이동한다")
    void allowedTransitions() {
        List<OrderStatus> flow = List.of(RECEIVED, OUTBOUND_WAITING, PICKING, PICKED, PACKED, LOADED,
                DISPATCHED, IN_DELIVERY, DELIVERED);
        for (int i = 0; i < flow.size() - 1; i++) {
            assertThat(flow.get(i).canMoveTo(flow.get(i + 1))).as(flow.get(i) + " → " + flow.get(i + 1)).isTrue();
        }
        assertThat(IN_DELIVERY.canMoveTo(FAILED)).isTrue();
        assertThat(DISPATCHED.canMoveTo(LOADED)).isTrue();
    }

    @Test
    @DisplayName("단계 건너뛰기와 역행은 허용하지 않는다")
    void forbiddenTransitions() {
        assertThat(RECEIVED.canMoveTo(PICKING)).isFalse();
        assertThat(PICKED.canMoveTo(LOADED)).isFalse();
        assertThat(PACKED.canMoveTo(PICKED)).isFalse();
        assertThat(LOADED.canMoveTo(IN_DELIVERY)).isFalse();
        assertThat(RECEIVED.canMoveTo(FAILED)).isFalse();
    }

    @Test
    @DisplayName("배송완료·배송실패는 종결 상태라 어디로도 이동할 수 없다")
    void terminalStates() {
        for (OrderStatus to : OrderStatus.values()) {
            assertThat(DELIVERED.canMoveTo(to)).isFalse();
            assertThat(FAILED.canMoveTo(to)).isFalse();
        }
    }
}
