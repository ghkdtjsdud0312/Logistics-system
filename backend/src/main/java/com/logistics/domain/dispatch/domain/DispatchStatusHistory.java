package com.logistics.domain.dispatch.domain;

import com.logistics.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 배차 상태 변경 이력 (행위자/이전·이후 상태/시각/설명) */
@Getter
@Entity
@Table(name = "dispatch_status_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DispatchStatusHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long dispatchId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DispatchStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DispatchStatus toStatus;

    @Column(nullable = false)
    private String actor;

    private String description;

    @Builder
    public DispatchStatusHistory(Long dispatchId, DispatchStatus fromStatus, DispatchStatus toStatus,
                                  String actor, String description) {
        this.dispatchId = dispatchId;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.actor = actor;
        this.description = description;
    }
}
