package com.logistics.domain.dispatch.domain;

import com.logistics.global.common.BaseTimeEntity;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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
    private Long vehicleId;

    @Column(nullable = false)
    private Long driverId;

    /** 하나의 출고 계획은 평생 하나의 배차에만 속할 수 있다 (DB unique 제약으로 동시 중복 배정 차단). */
    @ElementCollection
    @CollectionTable(name = "dispatch_outbound_link", joinColumns = @JoinColumn(name = "dispatch_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = "outbound_id"))
    @Column(name = "outbound_id", nullable = false)
    private List<Long> outboundIds = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime plannedAt;

    @Column(nullable = false)
    private double totalWeightKg;

    @Column(nullable = false)
    private double totalVolumeM3;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DispatchStatus status;

    private String routeAlgorithm;
    private Double initialDistanceKm;
    private Double optimizedDistanceKm;
    private Double improvementRate;
    private LocalDateTime routeComputedAt;

    @OneToMany(mappedBy = "dispatch", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sequence ASC")
    private List<RouteStop> stops = new ArrayList<>();

    @Version
    private Long version;

    @Builder
    public Dispatch(Long vehicleId, Long driverId, List<Long> outboundIds, LocalDateTime plannedAt,
                     double totalWeightKg, double totalVolumeM3) {
        this.vehicleId = vehicleId;
        this.driverId = driverId;
        this.outboundIds = outboundIds;
        this.plannedAt = plannedAt;
        this.totalWeightKg = totalWeightKg;
        this.totalVolumeM3 = totalVolumeM3;
        this.status = DispatchStatus.CONFIRMED;
    }

    public DispatchWindow window() {
        return DispatchWindow.of(this.plannedAt);
    }

    /** 경로 계산 결과를 반영한다. 기존 경로가 있으면 대체한다 (배차 물량은 확정 후 변경되지 않으므로 재계산 시 항상 최신 상태). */
    public void applyRoute(String algorithm, double initialDistanceKm, double optimizedDistanceKm, List<Waypoint> orderedRoute) {
        // orphanRemoval 컬렉션은 참조 교체가 금지되어 있어 제자리에서 비운다.
        // 비어있는 컬렉션에 clear()를 호출하면 Hibernate가 초기화에 쓰는 불변 빈 리스트 때문에 예외가 나므로 가드한다.
        if (!this.stops.isEmpty()) {
            this.stops.clear();
        }
        for (int i = 0; i < orderedRoute.size(); i++) {
            Waypoint waypoint = orderedRoute.get(i);
            double distanceFromPrevious = i == 0 ? 0.0 : orderedRoute.get(i - 1).distanceTo(waypoint);
            this.stops.add(new RouteStop(this, i, waypoint, distanceFromPrevious));
        }
        this.routeAlgorithm = algorithm;
        this.initialDistanceKm = initialDistanceKm;
        this.optimizedDistanceKm = optimizedDistanceKm;
        this.improvementRate = initialDistanceKm == 0 ? 0.0 : (initialDistanceKm - optimizedDistanceKm) / initialDistanceKm;
        this.routeComputedAt = LocalDateTime.now();
    }

    public RouteStop findStop(Long stopId) {
        return stops.stream().filter(stop -> stop.getId().equals(stopId)).findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.ROUTE_STOP_NOT_FOUND));
    }

    public boolean allStopsDelivered() {
        return !stops.isEmpty() && stops.stream().allMatch(stop -> stop.getStatus() == RouteStopStatus.DELIVERED);
    }

    /** 다음 상태로만 전이 가능하며, COMPLETED는 모든 경유지가 배송 완료된 경우에만 허용한다. */
    public void changeStatus(DispatchStatus next) {
        if (!this.status.canTransitionTo(next)) {
            throw new BusinessException(ErrorCode.DISPATCH_INVALID_STATUS_TRANSITION);
        }
        if (next == DispatchStatus.COMPLETED && !allStopsDelivered()) {
            throw new BusinessException(ErrorCode.DISPATCH_STOPS_NOT_DELIVERED);
        }
        this.status = next;
    }
}
