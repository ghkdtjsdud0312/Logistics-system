package com.logistics.domain.dispatch.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** 배송 허브(출발지) 좌표 - ADR-008: 다중 허브는 범위 밖, 고정 설정값 사용 */
@Component
public class HubLocation {

    private final double latitude;
    private final double longitude;

    public HubLocation(@Value("${hub.latitude:37.5665}") double latitude,
                        @Value("${hub.longitude:126.9780}") double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Waypoint toWaypoint() {
        return new Waypoint(null, "허브", latitude, longitude);
    }
}
