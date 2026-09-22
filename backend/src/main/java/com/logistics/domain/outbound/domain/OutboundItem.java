package com.logistics.domain.outbound.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 출고 계획에 포함된 입고 물량 한 줄
 * - inboundId는 다른 도메인(Inbound)의 식별자 참조일 뿐, FK 제약이나 연관관계를 걸지 않는다.
 */
@Getter
@Entity
@Table(name = "outbound_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboundItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outbound_id", nullable = false)
    private Outbound outbound;

    @Column(nullable = false)
    private Long inboundId;

    @Column(nullable = false)
    private int quantity;

    OutboundItem(Outbound outbound, Long inboundId, int quantity) {
        this.outbound = outbound;
        this.inboundId = inboundId;
        this.quantity = quantity;
    }
}
