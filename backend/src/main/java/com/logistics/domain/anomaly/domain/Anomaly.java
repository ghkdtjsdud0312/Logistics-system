package com.logistics.domain.anomaly.domain;

import com.logistics.global.common.BaseTimeEntity;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 이상 탐지 기록 (거부형: 명령 실패 시 동기 기록 / 지연형: 스케줄러가 기록) */
@Getter
@Entity
@Table(name = "anomaly")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Anomaly extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnomalyType type;

    /** 배차 확정 자체가 거부된 경우(아직 Dispatch가 없음)엔 null일 수 있다. */
    private Long dispatchId;

    /** 동일 원인 중복 알림 억제용 (예: "OVER_CAPACITY:100") */
    @Column(nullable = false)
    private String fingerprint;

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnomalyStatus status;

    @Builder
    public Anomaly(AnomalyType type, Long dispatchId, String fingerprint, String message) {
        this.type = type;
        this.dispatchId = dispatchId;
        this.fingerprint = fingerprint;
        this.message = message;
        this.status = AnomalyStatus.OPEN;
    }

    public void changeStatus(AnomalyStatus next) {
        if (!this.status.canTransitionTo(next)) {
            throw new BusinessException(ErrorCode.ANOMALY_INVALID_STATUS_TRANSITION);
        }
        this.status = next;
    }
}
