package com.logistics.domain.dispatch;

import com.logistics.domain.dispatch.domain.GeoDistance;
import com.logistics.domain.dispatch.domain.RouteOptimizer;
import com.logistics.global.geocoding.Coordinate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class RouteOptimizerTest {

    private static final Coordinate ORIGIN = new Coordinate(37.5, 127.0);

    @Test
    @DisplayName("직선거리는 서울-부산 약 325km이고 같은 점은 0이다")
    void distance() {
        Coordinate seoul = new Coordinate(37.5665, 126.9780);
        Coordinate busan = new Coordinate(35.1796, 129.0756);
        assertThat(GeoDistance.km(seoul, busan)).isCloseTo(325, within(5.0));
        assertThat(GeoDistance.km(seoul, seoul)).isZero();
    }

    @Test
    @DisplayName("한 줄로 늘어선 배송지는 가까운 곳부터 방문하고 빈 목록은 빈 경로다")
    void line() {
        List<Coordinate> stops = List.of(new Coordinate(37.5, 127.3), new Coordinate(37.5, 127.1),
                new Coordinate(37.5, 127.2));
        assertThat(RouteOptimizer.optimize(ORIGIN, stops)).containsExactly(1, 2, 0);
        assertThat(RouteOptimizer.optimize(ORIGIN, List.of())).isEmpty();
    }

    @Test
    @DisplayName("결과는 모든 지점을 한 번씩 포함하고 어떤 구간을 뒤집어도 더 짧아지지 않는다(2-opt 지역 최적)")
    void localOptimum() {
        List<Coordinate> stops = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            stops.add(new Coordinate(37.0 + ((i * 7) % 10) * 0.05, 126.5 + ((i * 3) % 9) * 0.07));
        }
        List<Integer> route = RouteOptimizer.optimize(ORIGIN, stops);
        assertThat(route).containsExactlyInAnyOrder(0, 1, 2, 3, 4, 5, 6, 7, 8);

        double best = total(stops, route);
        for (int i = 0; i < route.size() - 1; i++) {
            for (int j = i + 1; j < route.size(); j++) {
                List<Integer> reversed = new ArrayList<>(route);
                java.util.Collections.reverse(reversed.subList(i, j + 1));
                assertThat(total(stops, reversed)).isGreaterThanOrEqualTo(best - 1e-9);
            }
        }
    }

    private double total(List<Coordinate> stops, List<Integer> route) {
        return RouteOptimizer.totalKm(ORIGIN, route.stream().map(stops::get).toList());
    }
}
