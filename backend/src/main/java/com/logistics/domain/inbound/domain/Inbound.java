package com.logistics.domain.inbound.domain;

import com.logistics.global.common.BaseTimeEntity;
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

    @Builder
    public Inbound(String itemName, int quantity, String warehouseLocation) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.warehouseLocation = warehouseLocation;
        this.status = InboundStatus.REQUESTED;
    }

    public void complete() {
        this.status = InboundStatus.COMPLETED;
    }

    public void cancel() {
        this.status = InboundStatus.CANCELLED;
    }
}
