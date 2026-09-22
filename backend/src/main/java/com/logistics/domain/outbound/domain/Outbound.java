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
import java.util.Map;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboundStatus status;

    @OneToMany(mappedBy = "outbound", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OutboundItem> items = new ArrayList<>();

    private Outbound(String destination) {
        this.destination = destination;
        this.status = OutboundStatus.REQUESTED;
    }

    /** 출고 계획 생성: 도착지와 {입고ID: 수량} 목록으로 items를 함께 구성한다. */
    public static Outbound create(String destination, Map<Long, Integer> inboundQuantities) {
        Outbound outbound = new Outbound(destination);
        inboundQuantities.forEach((inboundId, quantity) ->
                outbound.items.add(new OutboundItem(outbound, inboundId, quantity)));
        return outbound;
    }

    public int totalQuantity() {
        return items.stream().mapToInt(OutboundItem::getQuantity).sum();
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
