package com.logistics.domain.returns.domain;

import com.logistics.domain.loading.domain.FailReason;
import com.logistics.global.common.BaseTimeEntity;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.logistics.domain.returns.domain.ReturnStatus.*;

/** 배송 실패로 자동 생성되는 반품. 파손이 아니면 반품입고 시 재고를 복구한다. */
@Getter
@Entity
@Table(name = "return_order")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReturnOrder extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String returnNo;

    private Long orderId;

    @Column(unique = true)
    private Long shipmentId;

    private int quantity;
    private Long locationId;

    @Enumerated(EnumType.STRING)
    private FailReason reason;

    @Enumerated(EnumType.STRING)
    private ReturnStatus status = REQUESTED;

    public ReturnOrder(Long orderId, Long shipmentId, FailReason reason, int quantity) {
        this.orderId = orderId;
        this.shipmentId = shipmentId;
        this.reason = reason;
        this.quantity = quantity;
    }

    public void assignNo() {
        this.returnNo = String.format("RTN-%03d", id);
    }

    /** 상품 파손이 아닐 때만 재고를 복구한다. */
    public boolean restocks() {
        return reason != FailReason.DAMAGED;
    }

    public void collect() {
        moveTo(REQUESTED, COLLECTING);
    }

    public void collected() {
        moveTo(COLLECTING, COLLECTED);
    }

    public void receive(Long locationId) {
        if (status != COLLECTED) {
            throw new BusinessException(ErrorCode.INVALID_RETURN_TRANSITION);
        }
        if (restocks() && locationId == null) {
            throw new BusinessException(ErrorCode.RETURN_LOCATION_REQUIRED);
        }
        moveTo(COLLECTED, RETURN_RECEIVED);
        this.locationId = restocks() ? locationId : null;
    }

    public void complete() {
        moveTo(RETURN_RECEIVED, COMPLETED);
    }

    private void moveTo(ReturnStatus from, ReturnStatus to) {
        if (status != from) {
            throw new BusinessException(ErrorCode.INVALID_RETURN_TRANSITION);
        }
        this.status = to;
    }
}
