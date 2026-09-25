package com.logistics.domain.order.domain;

import com.logistics.global.common.BaseTimeEntity;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** 주문 Aggregate. 상태 전이는 OrderStatus 규칙으로만 가능하다. */
@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String orderNo;

    private String customerName;
    private String address;
    private String phone;
    private LocalDateTime orderedAt;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.RECEIVED;

    @Version
    private Long version;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public Order(String customerName, String address, String phone, LocalDateTime orderedAt) {
        this.customerName = customerName;
        this.address = address;
        this.phone = phone;
        this.orderedAt = orderedAt;
    }

    public void addItem(Long productId, int quantity) {
        items.add(new OrderItem(this, productId, quantity));
    }

    /** 저장 후 발급된 ID로 업무번호(ORD-001)를 부여한다. */
    public void assignNo() {
        this.orderNo = String.format("ORD-%03d", id);
    }

    public int getTotalQuantity() {
        return items.stream().mapToInt(OrderItem::getQuantity).sum();
    }

    public void moveTo(OrderStatus to) {
        if (!status.canMoveTo(to)) {
            throw new BusinessException(ErrorCode.INVALID_ORDER_TRANSITION);
        }
        this.status = to;
    }
}
