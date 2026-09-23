package com.logistics.domain.dispatch.domain;

import java.util.List;

/** 경로(순서가 있는 경유지 목록)의 총 이동거리 계산 순수 함수 */
public final class RouteDistanceCalculator {

    private RouteDistanceCalculator() {
    }

    public static double totalDistanceKm(List<Waypoint> route) {
        double total = 0;
        for (int i = 1; i < route.size(); i++) {
            total += route.get(i - 1).distanceTo(route.get(i));
        }
        return total;
    }
}
