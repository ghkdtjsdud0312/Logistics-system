package com.logistics.domain.inventory.domain;

import com.logistics.global.common.BaseTimeEntity;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 상품+위치 단위 재고. 가용재고 = 현재재고 - 예약재고 */
@Getter
@Entity
@Table(name = "stock", uniqueConstraints = @UniqueConstraint(columnNames = {"productId", "locationId"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private Long locationId;
    private int onHand;
    private int reserved;

    @Version
    private Long version;

    public Stock(Long productId, Long locationId) {
        this.productId = productId;
        this.locationId = locationId;
    }

    public int getAvailable() {
        return onHand - reserved;
    }

    public void increase(int quantity) {
        requirePositive(quantity);
        onHand += quantity;
    }

    public void reserve(int quantity) {
        requirePositive(quantity);
        if (quantity > getAvailable()) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
        }
        reserved += quantity;
    }

    /** 피킹 완료: 예약분을 현재재고에서 함께 차감한다. */
    public void consumeReserved(int quantity) {
        requirePositive(quantity);
        if (quantity > reserved) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
        }
        reserved -= quantity;
        onHand -= quantity;
    }

    private void requirePositive(int quantity) {
        if (quantity <= 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }
}
