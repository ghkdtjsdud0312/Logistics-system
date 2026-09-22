package com.logistics.domain.dispatch.domain;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Nearest Neighbor(최근접 이웃) 알고리즘 기반 배차 경로 최적화 도메인 서비스
 * - 시작 경유지에서 가장 가까운 미방문 경유지를 순서대로 선택하여 방문 순서를 결정
 * - 정확한 최적해(TSP 최적해)는 아니지만 계산 비용이 낮아 실시간 배차에 적합
 */
@Component
public class NearestNeighborDispatchOptimizer {

    public List<Waypoint> optimize(List<Waypoint> waypoints) {
        if (waypoints == null || waypoints.isEmpty()) {
            return List.of();
        }

        List<Waypoint> remaining = new ArrayList<>(waypoints);
        List<Waypoint> route = new ArrayList<>();

        Waypoint current = remaining.remove(0);
        route.add(current);

        while (!remaining.isEmpty()) {
            Waypoint nearest = findNearest(current, remaining);
            remaining.remove(nearest);
            route.add(nearest);
            current = nearest;
        }

        return route;
    }

    private Waypoint findNearest(Waypoint from, List<Waypoint> candidates) {
        Waypoint nearest = candidates.get(0);
        double minDistance = from.distanceTo(nearest);

        for (Waypoint candidate : candidates) {
            double distance = from.distanceTo(candidate);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = candidate;
            }
        }
        return nearest;
    }
}
