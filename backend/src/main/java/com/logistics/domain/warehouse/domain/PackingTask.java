package com.logistics.domain.warehouse.domain;

import com.logistics.global.common.BaseTimeEntity;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 주문 1건당 1개의 포장 작업. 박스 코드를 입력해 완료한다. */
@Getter
@Entity
@Table(name = "packing_task")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PackingTask extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String taskNo;

    @Column(unique = true)
    private Long orderId;

    private String boxCode;

    @Enumerated(EnumType.STRING)
    private WorkStatus status = WorkStatus.WAITING;

    public PackingTask(Long orderId) {
        this.orderId = orderId;
    }

    public void assignNo() {
        this.taskNo = String.format("PACK-%03d", id);
    }

    public void complete(String boxCode) {
        if (status != WorkStatus.WAITING) {
            throw new BusinessException(ErrorCode.INVALID_TASK_TRANSITION);
        }
        this.boxCode = boxCode;
        this.status = WorkStatus.COMPLETED;
    }
}
