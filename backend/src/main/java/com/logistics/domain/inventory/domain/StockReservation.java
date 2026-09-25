package com.logistics.domain.inventory.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 주문 품목이 어느 위치에서 몇 개 예약되었는지 기록한다. 피킹 작업의 근거가 된다. */
@Getter
@Entity
@Table(name = "stock_reservation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private Long orderItemId;
    private Long productId;
    private Long locationId;
    private int quantity;

    public StockReservation(Long orderId, Long orderItemId, Long productId, Long locationId, int quantity) {
        this.orderId = orderId;
        this.orderItemId = orderItemId;
        this.productId = productId;
        this.locationId = locationId;
        this.quantity = quantity;
    }
}
