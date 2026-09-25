package com.logistics.domain.dispatch.domain;

import com.logistics.global.common.BaseTimeEntity;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.logistics.domain.dispatch.domain.DispatchStatus.*;

/** 차량·기사와 Shipment 묶음을 연결하는 배차. Shipment는 dispatchId로 이 배차를 참조한다. */
@Getter
@Entity
@Table(name = "dispatch")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dispatch extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String dispatchNo;

    private Long vehicleId;
    private Long driverId;
    private LocalDateTime plannedStartAt;
    private LocalDateTime plannedArrivalAt;
    private LocalDateTime startedAt;
    private double totalWeightKg;

    @Enumerated(EnumType.STRING)
    private DispatchStatus status = REGISTERED;

    @Version
    private Long version;

    public Dispatch(Long vehicleId, Long driverId, LocalDateTime plannedStartAt,
                    LocalDateTime plannedArrivalAt, double totalWeightKg) {
        this.vehicleId = vehicleId;
        this.driverId = driverId;
        this.plannedStartAt = plannedStartAt;
        this.plannedArrivalAt = plannedArrivalAt;
        this.totalWeightKg = totalWeightKg;
    }

    public void assignNo() {
        this.dispatchNo = String.format("DSP-%03d", id);
    }

    public void start(LocalDateTime now) {
        moveTo(REGISTERED, IN_TRANSIT);
        this.startedAt = now;
    }

    public void cancel() {
        moveTo(REGISTERED, CANCELLED);
    }

    public void complete() {
        moveTo(IN_TRANSIT, COMPLETED);
    }

    private void moveTo(DispatchStatus from, DispatchStatus to) {
        if (status != from) {
            throw new BusinessException(ErrorCode.INVALID_DISPATCH_TRANSITION);
        }
        this.status = to;
    }
}
