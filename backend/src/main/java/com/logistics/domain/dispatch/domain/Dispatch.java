package com.logistics.domain.dispatch.domain;

import com.logistics.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "dispatch")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dispatch extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String driverName;

    @Column(nullable = false)
    private String vehicleNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DispatchStatus status;

    /**
     * 최적화 이전 원본 경유지 순서
     */
    @ElementCollection
    @CollectionTable(name = "dispatch_waypoint", joinColumns = @JoinColumn(name = "dispatch_id"))
    @OrderColumn(name = "waypoint_order")
    private List<Waypoint> waypoints = new ArrayList<>();

    /**
     * Nearest Neighbor 알고리즘으로 최적화된 경유지 순서
     */
    @ElementCollection
    @CollectionTable(name = "dispatch_optimized_route", joinColumns = @JoinColumn(name = "dispatch_id"))
    @OrderColumn(name = "route_order")
    private List<Waypoint> optimizedRoute = new ArrayList<>();

    @Builder
    public Dispatch(String driverName, String vehicleNumber, List<Waypoint> waypoints) {
        this.driverName = driverName;
        this.vehicleNumber = vehicleNumber;
        this.waypoints = waypoints;
        this.status = DispatchStatus.PLANNED;
    }

    public void applyOptimizedRoute(List<Waypoint> optimizedRoute) {
        this.optimizedRoute = optimizedRoute;
        this.status = DispatchStatus.OPTIMIZED;
    }

    public void start() {
        this.status = DispatchStatus.IN_TRANSIT;
    }

    public void complete() {
        this.status = DispatchStatus.COMPLETED;
    }
}
