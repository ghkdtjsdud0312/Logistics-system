package com.logistics.domain.dispatch.domain;

import com.logistics.global.geocoding.Coordinate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 출발지에서 시작해 모든 배송지를 한 번씩 방문하는 순서를 구한다. Nearest Neighbor로 만들고 2-opt로 개선한다. */
public final class RouteOptimizer {

    private static final double EPS = 1e-9;

    private RouteOptimizer() {
    }

    /** stops의 인덱스를 방문 순서대로 돌려준다. */
    public static List<Integer> optimize(Coordinate origin, List<Coordinate> stops) {
        List<Integer> route = nearestNeighbor(origin, stops);
        boolean improved = true;
        while (improved) {
            improved = false;
            for (int i = 0; i < route.size() - 1; i++) {
                for (int j = i + 1; j < route.size(); j++) {
                    if (reverseGain(origin, stops, route, i, j) > EPS) {
                        Collections.reverse(route.subList(i, j + 1));
                        improved = true;
                    }
                }
            }
        }
        return route;
    }

    /** 출발지 → 순서대로 방문하는 총 거리(km). 출발지로 돌아오는 구간은 포함하지 않는다. */
    public static double totalKm(Coordinate origin, List<Coordinate> orderedStops) {
        double total = 0;
        Coordinate prev = origin;
        for (Coordinate stop : orderedStops) {
            total += GeoDistance.km(prev, stop);
            prev = stop;
        }
        return total;
    }

    private static List<Integer> nearestNeighbor(Coordinate origin, List<Coordinate> stops) {
        List<Integer> remaining = new ArrayList<>();
        for (int i = 0; i < stops.size(); i++) {
            remaining.add(i);
        }
        List<Integer> route = new ArrayList<>();
        Coordinate current = origin;
        while (!remaining.isEmpty()) {
            int best = remaining.get(0);
            for (int candidate : remaining) {
                if (GeoDistance.km(current, stops.get(candidate)) < GeoDistance.km(current, stops.get(best))) {
                    best = candidate;
                }
            }
            route.add(best);
            remaining.remove(Integer.valueOf(best));
            current = stops.get(best);
        }
        return route;
    }

    /** route[i..j] 구간을 뒤집었을 때 줄어드는 거리. 양수면 뒤집는 편이 낫다. */
    private static double reverseGain(Coordinate origin, List<Coordinate> stops, List<Integer> route, int i, int j) {
        Coordinate prev = i == 0 ? origin : stops.get(route.get(i - 1));
        Coordinate first = stops.get(route.get(i));
        Coordinate last = stops.get(route.get(j));
        double before = GeoDistance.km(prev, first);
        double after = GeoDistance.km(prev, last);
        if (j < route.size() - 1) {
            Coordinate next = stops.get(route.get(j + 1));
            before += GeoDistance.km(last, next);
            after += GeoDistance.km(first, next);
        }
        return before - after;
    }
}
