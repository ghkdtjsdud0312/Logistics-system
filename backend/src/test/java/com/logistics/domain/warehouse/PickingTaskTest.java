package com.logistics.domain.warehouse;

import com.logistics.domain.warehouse.domain.PackingTask;
import com.logistics.domain.warehouse.domain.PickingTask;
import com.logistics.domain.warehouse.domain.WorkStatus;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PickingTaskTest {

    private PickingTask task() {
        return new PickingTask(1L, 1L, 1L, 1L, 10);
    }

    private ErrorCode codeOf(Runnable action) {
        return org.assertj.core.api.Assertions.catchThrowableOfType(action::run, BusinessException.class).getErrorCode();
    }

    @Test
    @DisplayName("피킹은 시작한 뒤 요청수량과 같은 수량으로만 완료할 수 있다")
    void complete_exactQuantity() {
        PickingTask task = task();
        task.start();
        task.complete(10);

        assertThat(task.getStatus()).isEqualTo(WorkStatus.COMPLETED);
        assertThat(task.getPickedQty()).isEqualTo(10);
    }

    @Test
    @DisplayName("요청수량보다 적으면 MISMATCH, 많으면 EXCEEDED로 거부하고 작업은 진행중으로 남는다")
    void complete_wrongQuantity() {
        PickingTask task = task();
        task.start();

        assertThat(codeOf(() -> task.complete(9))).isEqualTo(ErrorCode.PICKED_QTY_MISMATCH);
        assertThat(codeOf(() -> task.complete(11))).isEqualTo(ErrorCode.PICKED_QTY_EXCEEDED);
        assertThat(task.getStatus()).isEqualTo(WorkStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("시작하지 않은 작업 완료, 중복 시작, 완료 후 재완료는 거부한다")
    void invalidTransitions() {
        PickingTask task = task();
        assertThatThrownBy(() -> task.complete(10)).isInstanceOf(BusinessException.class);
        task.start();
        assertThatThrownBy(task::start).isInstanceOf(BusinessException.class);
        task.complete(10);
        assertThatThrownBy(() -> task.complete(10)).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("포장 작업은 한 번만 완료할 수 있다")
    void packing_onlyOnce() {
        PackingTask packing = new PackingTask(1L);
        packing.complete("BOX-001");

        assertThat(packing.getBoxCode()).isEqualTo("BOX-001");
        assertThatThrownBy(() -> packing.complete("BOX-002")).isInstanceOf(BusinessException.class);
    }
}
