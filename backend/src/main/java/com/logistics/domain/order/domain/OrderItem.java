package com.logistics.domain.order.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 주문 품목. 단계별 처리 수량(피킹/상차/배송)을 함께 기록한다. */
@Getter
@Entity
@Table(name = "order_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Order order;

    private Long productId;
    private int quantity;
    private int pickedQty;
    private int loadedQty;
    private int deliveredQty;

    OrderItem(Order order, Long productId, int quantity) {
        this.order = order;
        this.productId = productId;
        this.quantity = quantity;
    }

    public void recordPicked(int qty) {
        this.pickedQty += qty;
    }

    public void recordLoaded(int qty) {
        this.loadedQty += qty;
    }

    public void recordDelivered(int qty) {
        this.deliveredQty += qty;
    }
}
