package com.logistics.domain.outbound.domain;

import com.logistics.global.common.BaseTimeEntity;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 출고 계획 도메인 엔티티 (Aggregate Root)
 * - 하나의 출고 계획은 여러 입고 건에서 물량을 가져올 수 있다 (1:N).
 */
@Getter
@Entity
@Table(name = "outbound")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Outbound extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String destination;

    /** 배송지 좌표 (ADR-008) - Day 4 경로 최적화의 입력값 */
    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboundStatus status;

    @OneToMany(mappedBy = "outbound", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OutboundItem> items = new ArrayList<>();

    private Outbound(String destination, double latitude, double longitude) {
        this.destination = destination;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = OutboundStatus.REQUESTED;
    }

    /** 출고 계획 생성: 도착지/좌표와 품목별 입력값(입고ID/수량/중량/부피) 목록으로 items를 함께 구성한다. */
    public static Outbound create(String destination, double latitude, double longitude, List<OutboundItemInput> itemInputs) {
        Outbound outbound = new Outbound(destination, latitude, longitude);
        itemInputs.forEach(input -> outbound.items.add(new OutboundItem(
                outbound, input.inboundId(), input.quantity(), input.weightKg(), input.volumeM3())));
        return outbound;
    }

    public int totalQuantity() {
        return items.stream().mapToInt(OutboundItem::getQuantity).sum();
    }

    public double totalWeightKg() {
        return items.stream().mapToDouble(OutboundItem::getWeightKg).sum();
    }

    public double totalVolumeM3() {
        return items.stream().mapToDouble(OutboundItem::getVolumeM3).sum();
    }

    /** 피킹 시작: REQUESTED -> PICKING */
    public void pick() {
        if (this.status != OutboundStatus.REQUESTED) {
            throw new BusinessException(ErrorCode.OUTBOUND_INVALID_STATUS_TRANSITION);
        }
        this.status = OutboundStatus.PICKING;
    }

    /** 출고 완료: PICKING -> SHIPPED */
    public void ship() {
        if (this.status != OutboundStatus.PICKING) {
            throw new BusinessException(ErrorCode.OUTBOUND_INVALID_STATUS_TRANSITION);
        }
        this.status = OutboundStatus.SHIPPED;
    }

    public void cancel() {
        if (this.status == OutboundStatus.SHIPPED) {
            throw new BusinessException(ErrorCode.OUTBOUND_INVALID_STATUS_TRANSITION);
        }
        this.status = OutboundStatus.CANCELLED;
    }
}
