package com.logistics.domain.dispatch.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 배차 경유지 (위/경도 좌표)
 * - Nearest Neighbor 알고리즘의 입력 단위
 */
@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Waypoint {

    private String label;
    private double latitude;
    private double longitude;

    public double distanceTo(Waypoint other) {
        double dx = this.latitude - other.latitude;
        double dy = this.longitude - other.longitude;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
