package com.logistics.domain.inbound.domain;

import com.logistics.global.common.BaseTimeEntity;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static com.logistics.domain.inbound.domain.InboundStatus.*;

@Getter
@Entity
@Table(name = "inbound")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inbound extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String inboundNo;

    @Column(nullable = false)
    private String partnerName;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private LocalDate inboundDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InboundStatus status = EXPECTED;

    /** 적치 위치 (적치완료 후에만 값이 있다) */
    private Long locationId;

    public Inbound(String partnerName, Long productId, int quantity, LocalDate inboundDate) {
        if (quantity <= 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        this.partnerName = partnerName;
        this.productId = productId;
        this.quantity = quantity;
        this.inboundDate = inboundDate;
    }

    /** 저장 후 발급된 ID로 업무번호(IN-001)를 부여한다. */
    public void assignNo() {
        this.inboundNo = String.format("IN-%03d", id);
    }

    public void receive() {
        moveTo(EXPECTED, RECEIVED);
    }

    public void readyForPutaway() {
        moveTo(RECEIVED, PUTAWAY_WAITING);
    }

    public void putaway(Long locationId) {
        moveTo(PUTAWAY_WAITING, PUTAWAY_DONE);
        this.locationId = locationId;
    }

    private void moveTo(InboundStatus from, InboundStatus to) {
        if (status != from) {
            throw new BusinessException(ErrorCode.INBOUND_INVALID_STATUS_TRANSITION);
        }
        this.status = to;
    }
}
