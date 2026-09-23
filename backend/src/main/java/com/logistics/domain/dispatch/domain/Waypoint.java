package com.logistics.domain.dispatch.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 경로 계산용 경유지 (위/경도 좌표)
 * - outboundId가 null이면 허브(출발지), 아니면 해당 출고 계획의 배송지
 * - Nearest Neighbor / 2-opt 알고리즘의 입력·출력 단위
 */
@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Waypoint {

    private static final double EARTH_RADIUS_KM = 6371.0;

    private Long outboundId;
    private String label;
    private double latitude;
    private double longitude;

    /** Haversine 공식으로 두 위경도 좌표 사이의 거리(km)를 계산한다. */
    public double distanceTo(Waypoint other) {
        double dLat = Math.toRadians(other.latitude - this.latitude);
        double dLon = Math.toRadians(other.longitude - this.longitude);
        double lat1 = Math.toRadians(this.latitude);
        double lat2 = Math.toRadians(other.latitude);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
}
