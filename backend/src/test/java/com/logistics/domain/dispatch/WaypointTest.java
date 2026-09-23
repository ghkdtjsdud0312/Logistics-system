package com.logistics.domain.dispatch;

import com.logistics.domain.dispatch.domain.Waypoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class WaypointTest {

    @Test
    @DisplayName("서울시청-부산시청 간 Haversine 거리는 약 325km이다")
    void distanceTo_knownCoordinates() {
        Waypoint seoul = new Waypoint(null, "서울시청", 37.5665, 126.9780);
        Waypoint busan = new Waypoint(null, "부산시청", 35.1796, 129.0756);

        double distanceKm = seoul.distanceTo(busan);

        assertThat(distanceKm).isCloseTo(325, within(10.0));
    }

    @Test
    @DisplayName("같은 좌표 사이의 거리는 0이다")
    void distanceTo_samePoint_isZero() {
        Waypoint point = new Waypoint(1L, "A", 37.0, 127.0);

        assertThat(point.distanceTo(point)).isEqualTo(0.0);
    }
}
