package com.logistics.domain.outbound.domain;

import com.logistics.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "outbound")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Outbound extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private String destination;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboundStatus status;

    @Builder
    public Outbound(String itemName, int quantity, String destination) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.destination = destination;
        this.status = OutboundStatus.REQUESTED;
    }

    public void ship() {
        this.status = OutboundStatus.SHIPPED;
    }

    public void cancel() {
        this.status = OutboundStatus.CANCELLED;
    }
}
