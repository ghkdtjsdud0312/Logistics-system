package com.logistics.domain.loading.domain;

import com.logistics.global.common.BaseTimeEntity;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.logistics.domain.loading.domain.ShipmentStatus.*;

/** 주문 1건당 1개의 배송 단위. 상차 시 생성되고 배차에 묶인다. */
@Getter
@Entity
@Table(name = "shipment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shipment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long orderId;

    private Long dispatchId;
    private LocalDateTime deliveredAt;
    private String failDetail;

    @Enumerated(EnumType.STRING)
    private ShipmentStatus status = LOADED;

    @Enumerated(EnumType.STRING)
    private FailReason failReason;

    public Shipment(Long orderId) {
        this.orderId = orderId;
    }

    public void assignTo(Long dispatchId) {
        moveTo(LOADED, DISPATCHED);
        this.dispatchId = dispatchId;
    }

    public void unassign() {
        moveTo(DISPATCHED, LOADED);
        this.dispatchId = null;
    }

    public void startDelivery() {
        moveTo(DISPATCHED, IN_DELIVERY);
    }

    public void deliver(LocalDateTime at) {
        moveTo(IN_DELIVERY, DELIVERED);
        this.deliveredAt = at;
    }

    public void fail(FailReason reason, String detail) {
        moveTo(IN_DELIVERY, FAILED);
        this.failReason = reason;
        this.failDetail = detail;
    }

    public boolean isClosed() {
        return status == DELIVERED || status == FAILED;
    }

    private void moveTo(ShipmentStatus from, ShipmentStatus to) {
        if (status != from) {
            throw new BusinessException(ErrorCode.INVALID_SHIPMENT_TRANSITION);
        }
        this.status = to;
    }
}
