package com.logistics.domain.warehouse.domain;

import com.logistics.global.common.BaseTimeEntity;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 예약 위치별 피킹 작업. 피킹수량이 요청수량과 같을 때만 완료할 수 있다. */
@Getter
@Entity
@Table(name = "picking_task")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PickingTask extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String taskNo;

    private Long orderId;
    private Long orderItemId;
    private Long productId;
    private Long locationId;
    private int requestedQty;
    private int pickedQty;

    @Enumerated(EnumType.STRING)
    private WorkStatus status = WorkStatus.WAITING;

    public PickingTask(Long orderId, Long orderItemId, Long productId, Long locationId, int requestedQty) {
        this.orderId = orderId;
        this.orderItemId = orderItemId;
        this.productId = productId;
        this.locationId = locationId;
        this.requestedQty = requestedQty;
    }

    public void assignNo() {
        this.taskNo = String.format("PICK-%03d", id);
    }

    public void start() {
        requireStatus(WorkStatus.WAITING);
        this.status = WorkStatus.IN_PROGRESS;
    }

    public void complete(int quantity) {
        requireStatus(WorkStatus.IN_PROGRESS);
        if (quantity > requestedQty) {
            throw new BusinessException(ErrorCode.PICKED_QTY_EXCEEDED);
        }
        if (quantity != requestedQty) {
            throw new BusinessException(ErrorCode.PICKED_QTY_MISMATCH);
        }
        this.pickedQty = quantity;
        this.status = WorkStatus.COMPLETED;
    }

    private void requireStatus(WorkStatus expected) {
        if (status != expected) {
            throw new BusinessException(ErrorCode.INVALID_TASK_TRANSITION);
        }
    }
}
