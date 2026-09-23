package com.logistics.domain.dispatch.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 조회 전용 투영(read projection): HistoryConsumer가 Kafka 이벤트를 소비해 멱등 갱신한다.
 * 트랜잭션 조회용 Dispatch 테이블과 별개로, 이벤트 기반 후속 처리가 실제로 동작함을 보여주기 위한 산출물이다.
 */
@Getter
@Entity
@Table(name = "dispatch_status_projection")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DispatchStatusProjection {

    @Id
    private Long dispatchId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DispatchStatus currentStatus;

    @Column(nullable = false)
    private long lastEventVersion;

    @Column(nullable = false)
    private LocalDateTime lastEventAt;

    public static DispatchStatusProjection of(Long dispatchId, DispatchStatus status, long version, LocalDateTime eventAt) {
        DispatchStatusProjection projection = new DispatchStatusProjection();
        projection.dispatchId = dispatchId;
        projection.apply(status, version, eventAt);
        return projection;
    }

    public void apply(DispatchStatus status, long version, LocalDateTime eventAt) {
        this.currentStatus = status;
        this.lastEventVersion = version;
        this.lastEventAt = eventAt;
    }
}
