package com.logistics.domain.dispatch;

import com.logistics.domain.dispatch.domain.RouteDistanceCalculator;
import com.logistics.domain.dispatch.domain.TwoOptRouteImprover;
import com.logistics.domain.dispatch.domain.Waypoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TwoOptRouteImproverTest {

    private final TwoOptRouteImprover improver = new TwoOptRouteImprover();

    @Test
    @DisplayName("2-opt 개선 결과는 초기 경로보다 총 거리가 길지 않다")
    void improve_neverIncreasesDistance() {
        List<Waypoint> initial = List.of(
                new Waypoint(null, "허브", 37.50, 127.00),
                new Waypoint(1L, "A", 37.60, 127.10),
                new Waypoint(2L, "B", 37.40, 126.90),
                new Waypoint(3L, "C", 37.55, 127.05),
                new Waypoint(4L, "D", 37.45, 126.95)
        );

        List<Waypoint> improved = improver.improve(initial);

        double initialDistance = RouteDistanceCalculator.totalDistanceKm(initial);
        double improvedDistance = RouteDistanceCalculator.totalDistanceKm(improved);
        assertThat(improvedDistance).isLessThanOrEqualTo(initialDistance);
    }

    @Test
    @DisplayName("허브(첫 번째 경유지)는 개선 후에도 그대로 첫 노드이다")
    void improve_keepsHubFirst() {
        List<Waypoint> initial = List.of(
                new Waypoint(null, "허브", 0, 0),
                new Waypoint(1L, "A", 10, 10),
                new Waypoint(2L, "B", 1, 1),
                new Waypoint(3L, "C", 5, 5)
        );

        List<Waypoint> improved = improver.improve(initial);

        assertThat(improved.get(0).getLabel()).isEqualTo("허브");
        assertThat(improved).hasSize(initial.size());
    }

    @Test
    @DisplayName("모든 경유지가 정확히 한 번씩 포함된다")
    void improve_containsAllWaypointsExactlyOnce() {
        List<Waypoint> initial = List.of(
                new Waypoint(null, "허브", 0, 0),
                new Waypoint(1L, "A", 10, 10),
                new Waypoint(2L, "B", 1, 1),
                new Waypoint(3L, "C", 5, 5)
        );

        List<Waypoint> improved = improver.improve(initial);

        assertThat(improved).extracting(Waypoint::getOutboundId)
                .containsExactlyInAnyOrder(null, 1L, 2L, 3L);
    }
}
