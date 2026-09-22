package com.logistics.domain.dispatch;

import com.logistics.domain.dispatch.domain.NearestNeighborDispatchOptimizer;
import com.logistics.domain.dispatch.domain.Waypoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NearestNeighborDispatchOptimizerTest {

    private final NearestNeighborDispatchOptimizer optimizer = new NearestNeighborDispatchOptimizer();

    @Test
    @DisplayName("가장 가까운 경유지 순서로 경로를 정렬한다")
    void optimize() {
        List<Waypoint> waypoints = List.of(
                new Waypoint("창고", 0, 0),
                new Waypoint("C", 10, 10),
                new Waypoint("A", 1, 1),
                new Waypoint("B", 2, 2)
        );

        List<Waypoint> result = optimizer.optimize(waypoints);

        assertThat(result).extracting(Waypoint::getLabel)
                .containsExactly("창고", "A", "B", "C");
    }
}
