package com.logistics.domain.inbound.domain;

import com.logistics.global.common.BaseTimeEntity;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 입고 도메인 엔티티
 */
@Getter
@Entity
@Table(name = "inbound")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inbound extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private String warehouseLocation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InboundStatus status;

    /** 검수 완료 시에만 값이 채워지는 실 수령 수량 */
    private Integer inspectedQuantity;

    @Builder
    public Inbound(String itemName, int quantity, String warehouseLocation) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.warehouseLocation = warehouseLocation;
        this.status = InboundStatus.REQUESTED;
    }

    /** 입고 처리 시작: REQUESTED -> IN_PROGRESS */
    public void start() {
        if (this.status != InboundStatus.REQUESTED) {
            throw new BusinessException(ErrorCode.INBOUND_INVALID_STATUS_TRANSITION);
        }
        this.status = InboundStatus.IN_PROGRESS;
    }

    /** 검수 완료: IN_PROGRESS -> COMPLETED, 검수 수량 기록 */
    public void complete(int inspectedQuantity) {
        if (this.status != InboundStatus.IN_PROGRESS) {
            throw new BusinessException(ErrorCode.INBOUND_INVALID_STATUS_TRANSITION);
        }
        this.inspectedQuantity = inspectedQuantity;
        this.status = InboundStatus.COMPLETED;
    }

    public void cancel() {
        if (this.status == InboundStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.INBOUND_INVALID_STATUS_TRANSITION);
        }
        this.status = InboundStatus.CANCELLED;
    }
}
