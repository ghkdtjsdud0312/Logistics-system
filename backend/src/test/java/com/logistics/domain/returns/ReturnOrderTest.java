package com.logistics.domain.returns;

import com.logistics.domain.loading.domain.FailReason;
import com.logistics.domain.returns.domain.ReturnOrder;
import com.logistics.domain.returns.domain.ReturnStatus;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class ReturnOrderTest {

    private ReturnOrder returnOf(FailReason reason) {
        return new ReturnOrder(1L, 1L, reason, 5);
    }

    @Test
    @DisplayName("회수요청 → 회수중 → 회수완료 → 반품입고 → 처리완료 순서로만 진행한다")
    void flow() {
        ReturnOrder r = returnOf(FailReason.CUSTOMER_ABSENT);
        assertThatThrownBy(r::collected).isInstanceOf(BusinessException.class);
        r.collect();
        r.collected();
        r.receive(1L);
        r.complete();

        assertThat(r.getStatus()).isEqualTo(ReturnStatus.COMPLETED);
        assertThatThrownBy(r::complete).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("상품 파손이 아니면 반품입고에 위치가 필요하고 재고를 복구하며, 파손이면 복구하지 않는다")
    void restock_rules() {
        ReturnOrder normal = returnOf(FailReason.REFUSED);
        normal.collect();
        normal.collected();
        assertThat(catchThrowableOfType(() -> normal.receive(null), BusinessException.class).getErrorCode())
                .isEqualTo(ErrorCode.RETURN_LOCATION_REQUIRED);
        assertThat(normal.restocks()).isTrue();

        ReturnOrder damaged = returnOf(FailReason.DAMAGED);
        damaged.collect();
        damaged.collected();
        damaged.receive(7L);
        assertThat(damaged.restocks()).isFalse();
        assertThat(damaged.getLocationId()).isNull();
        assertThat(damaged.getStatus()).isEqualTo(ReturnStatus.RETURN_RECEIVED);
    }
}
