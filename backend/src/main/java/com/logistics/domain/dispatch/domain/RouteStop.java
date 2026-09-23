package com.logistics.domain.dispatch.domain;

import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 배차 경로 상의 경유지 한 곳 (허브 또는 배송지)
 */
@Getter
@Entity
@Table(name = "route_stop")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RouteStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_id", nullable = false)
    private Dispatch dispatch;

    @Column(nullable = false)
    private int sequence;

    /** null이면 허브, 아니면 해당 출고 계획 ID */
    private Long outboundId;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private double distanceFromPreviousKm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RouteStopStatus status;

    RouteStop(Dispatch dispatch, int sequence, Waypoint waypoint, double distanceFromPreviousKm) {
        this.dispatch = dispatch;
        this.sequence = sequence;
        this.outboundId = waypoint.getOutboundId();
        this.label = waypoint.getLabel();
        this.latitude = waypoint.getLatitude();
        this.longitude = waypoint.getLongitude();
        this.distanceFromPreviousKm = distanceFromPreviousKm;
        this.status = RouteStopStatus.PENDING;
    }

    public void changeStatus(RouteStopStatus next) {
        if (!this.status.canTransitionTo(next)) {
            throw new BusinessException(ErrorCode.ROUTE_STOP_INVALID_TRANSITION);
        }
        this.status = next;
    }
}
