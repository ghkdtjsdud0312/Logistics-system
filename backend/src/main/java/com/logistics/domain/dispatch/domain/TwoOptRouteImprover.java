package com.logistics.domain.dispatch.domain;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 2-opt 경로 개선 도메인 서비스
 * - 허브(0번 인덱스)는 고정하고, 두 간선을 교환했을 때 총 거리가 줄어드는 경우에만 반영한다.
 * - 반복 상한을 두어 동일 입력에 항상 같은 결과를, 유한 시간 안에 보장한다.
 */
@Component
public class TwoOptRouteImprover {

    private static final int MAX_ITERATIONS = 1000;

    public List<Waypoint> improve(List<Waypoint> initialRoute) {
        List<Waypoint> best = new ArrayList<>(initialRoute);
        if (best.size() < 4) {
            return best;
        }

        boolean improved = true;
        int iterations = 0;
        while (improved && iterations < MAX_ITERATIONS) {
            improved = false;
            iterations++;
            for (int i = 1; i < best.size() - 1; i++) {
                for (int j = i + 1; j < best.size(); j++) {
                    List<Waypoint> candidate = swap(best, i, j);
                    if (RouteDistanceCalculator.totalDistanceKm(candidate) < RouteDistanceCalculator.totalDistanceKm(best)) {
                        best = candidate;
                        improved = true;
                    }
                }
            }
        }
        return best;
    }

    private List<Waypoint> swap(List<Waypoint> route, int i, int j) {
        List<Waypoint> next = new ArrayList<>(route);
        List<Waypoint> segment = new ArrayList<>(next.subList(i, j + 1));
        Collections.reverse(segment);
        for (int k = 0; k < segment.size(); k++) {
            next.set(i + k, segment.get(k));
        }
        return next;
    }
}
